import {Injectable} from '@angular/core';
import {Alert, AlertType} from '../dtos/alert';
import {Observable, Subject} from 'rxjs';
import {filter} from 'rxjs/operators';
import {Globals, Utilities} from '../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  readonly globalAlert = 'global-alert';
  private subject = new Subject<Alert>();
  private defaultId = 'default-alert';

  alerts: Alert[] = [];


  constructor() { }

  onAlert(alertComponentId = this.defaultId): Observable<Alert> {
    DEBUG_LOG('Subscribed to alerts with component: ' + alertComponentId);
    return this.subject.asObservable().pipe(filter(al => al && (al.componentId === alertComponentId
      || al.componentId === this.globalAlert)));
  }

  getAlerts(alertComponentId = this.defaultId) {
    return this.alerts.filter(alert => alert && (alert.componentId === alertComponentId
    || alert.componentId === this.globalAlert));
  }

  alert(alert: Alert, sourceFn: string = 'No sourceFn given') {
    alert.componentId = alert.componentId || this.defaultId;

    DEBUG_LOG(alert.type + ' Alert for ComponentID: ' + alert.componentId + ' message: ' + alert.message + ' routeChange '
      + alert.keepAfterRouteChange);

    this.subject.next(alert);
    this.alerts.push(alert);
  }

  success(message: string, options?: any, sourceFn: string = 'No sourceFn given') {
    this.alert(new Alert({ ...options, type: AlertType.Success, message }), sourceFn);
  }

  error(message: string, options?: any, sourceFn: string = 'No sourceFn given') {
    this.alert(new Alert({ ...options, type: AlertType.Error, message }), sourceFn);
  }

  info(message: string, options?: any, sourceFn: string = 'No sourceFn given') {
    this.alert(new Alert({ ...options, type: AlertType.Info, message }), sourceFn);
  }

  warn(message: string, options?: any, sourceFn: string = 'No sourceFn given') {
    this.alert(new Alert({ ...options, type: AlertType.Warning, message }), sourceFn);
  }

  /**
   *
   * @param error the Error that of which to extract the message an Alert Status (e.g. 404 is warning).
   * @param componentId string Id of the ErrorComponent that will show the Error Message. This is set through \@Input alertComponentId.
   * and should most likely be named like the component where it is sent from or be the globalAlertId
   * @param sourceFn the Function in which the alertWasIssued. This will just be shown in the console in debug mode.
   * @param options type: AlertType, this will override the Type Extracted from the error.\n
   * keepAfterRouteChange: boolean, if true, the alert will stay when navigating to a different page that also has the alert component. \n
   * dismissible: boolean, if true, the alert will have an X button the remove the alert, otherwise it can't be removed by the user.
   */
  alertFromError(error: any, options?: any, sourceFn: string = 'No sourceFn given') {
    DEBUG_LOG('Alerting for Error of component: ' + options.componentID + ' from sourceFn: ' + sourceFn);
    console.log(error);
    let type = AlertType.Error;
    let message: string;
    if (typeof error.error === 'object') {
      if (error.error.status === 404) {
        type = AlertType.Warning;
      }
      message = error.error.error;
    } else {
      if (error.status === 404) {
        type = AlertType.Warning;
      }
      message = error.error;
    }

    this.alert(new Alert({ ...options, message: message, type: type}), sourceFn);
  }

  clear(correspondingAlertComponentId = this.defaultId) {
    this.subject.next(new Alert({componentId: correspondingAlertComponentId} ));
    this.alerts = this.alerts.filter(al => al.componentId !== correspondingAlertComponentId);
  }

  removeAlert(alert: Alert) {
    this.alerts.splice(this.alerts.indexOf(alert), 1);
  }
}
