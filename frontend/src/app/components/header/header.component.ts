import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  currentUser: string;

  constructor(public authService: AuthService, private router: Router) {
  }

  ngOnInit() {
    if (!this.isAdmin()) {
      this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
    }
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }
}
