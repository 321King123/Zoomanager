import {Component, Input, OnInit, OnDestroy} from '@angular/core';
import {Alert, AlertType} from '../../dtos/alert';
import {Subscription} from 'rxjs';
import {NavigationStart, Router} from '@angular/router';
import {AlertService} from '../../services/alert.service';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit, OnDestroy {
  @Input() alertComponentId;


  alerts: Alert[] = [];

  alertSubscription: Subscription;
  routeSubscription: Subscription;

  constructor(private router: Router, private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.alertSubscription = this.alertService.onAlert(this.alertComponentId)
      .subscribe(
        alert => {
          if (!alert.message) {
            this.alerts = this.alerts.filter(al => al.keepAfterRouteChange);

            this.alerts.forEach(al => delete al.keepAfterRouteChange);
            return;
          }

          this.alerts = this.alerts.filter((al) => (al.message !== alert.message && al.type === alert.type));
          this.alerts.push(alert);
        }
      );

    this.routeSubscription = this.router.events
      .subscribe(
        event => {
          if (event instanceof NavigationStart) {
            this.alertService.clear(this.alertComponentId);
          }
        }
    );

  }

  ngOnDestroy() {
    // unsubscribe to avoid memory leaks
    this.alertSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }


  closeAlert(alert: Alert) {
    this.alerts.splice(this.alerts.indexOf(alert), 1);
  }

  clearAlerts() {
    this.alertService.clear(this.alertComponentId);
  }
}
