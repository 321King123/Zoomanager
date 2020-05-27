import {Injectable} from '@angular/core';
import {Alert, AlertType} from '../dtos/alert';
import {Observable, Subject} from 'rxjs';
import {filter} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private subject = new Subject<Alert>();
  private defaultId = 'default-alert';


  constructor() { }

  onAlert(alertComponentId = this.defaultId): Observable<Alert> {
    return this.subject.asObservable().pipe(filter(al => al && al.correspondingAlertComponentId === alertComponentId));
  }

  alert(alert: Alert, source: string = 'No source given') {
    alert.correspondingAlertComponentId = alert.correspondingAlertComponentId || this.defaultId;
    this.subject.next(alert);
  }

  success(message: string, source: string = 'No source given', options?: any) {
    this.alert(new Alert({ ...options, type: AlertType.Success, message }));
  }

  error(message: string, source: string = 'No source given', options?: any) {
    this.alert(new Alert({ ...options, type: AlertType.Error, message }));
  }

  info(message: string, source: string = 'No source given', options?: any) {
    this.alert(new Alert({ ...options, type: AlertType.Info, message }));
  }

  warn(message: string, source: string = 'No source given', options?: any) {
    this.alert(new Alert({ ...options, type: AlertType.Warning, message }));
  }

  alertFromError(error: any, componentId: string, source: string = 'No source given', options?: any) {
    console.log('Alerting for Error of component: ' + componentId + ' from source: ' + source);
    console.log(error);
    if (typeof error.error === 'object') {
      this.subject.next(new Alert({ ...options, message: error.error.error, type: AlertType.Error,
        correspondingAlertComponentId: componentId}));
    } else {
      this.subject.next(new Alert({ ...options, message: error.error, type: AlertType.Error,
        correspondingAlertComponentId: componentId}));
    }
  }

  clear(correspondingAlertComponentId = this.defaultId) {
    this.subject.next(new Alert({correspondingAlertComponentId} ));
  }
}
