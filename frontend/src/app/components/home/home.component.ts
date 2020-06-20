import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {ZooInfoService} from '../../services/zooInfo.service';
import {ZooInfo} from '../../dtos/zooInfo';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {AlertService} from '../../services/alert.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(public authService: AuthService, public zooInfoService: ZooInfoService,
              private router: Router, private alertService: AlertService) { }
  zooInformation: ZooInfo;
  ready: boolean = false;

  ngOnInit() {
    this.getZooInformation();
  }

  getZooInformation() {

    this.zooInfoService.displayZooInfo().subscribe(
      zooInfo => {
        this.zooInformation = zooInfo;
        this.ready = true;
      },
      error => {
        DEBUG_LOG('Failed to load zoo information');
        if (this.isAdmin()) {this.alertService.alertFromError(error, {}, 'getZooInformation'); }
      }
    );
  }

  editZooInformation() {
    DEBUG_LOG('edit zoo info button');
    this.router.navigate(['edit']);
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }
}
