import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureService} from '../../services/enclosure.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';

@Component({
  selector: 'app-enclosure-view',
  templateUrl: './enclosure-view.component.html',
  styleUrls: ['./enclosure-view.component.css']
})
export class EnclosureViewComponent implements OnInit {

  error: boolean = false;
  errorMessage: string = '';

  enclosureToView: Enclosure;


  constructor(private enclosureService: EnclosureService, private authService: AuthService,
              private route: ActivatedRoute, private router: Router, private _location: Location) {

  }

  ngOnInit(): void {
    const enclsureToViewId = Number(this.route.snapshot.paramMap.get('enclosureId'));
    this.loadEnclosureToView(enclsureToViewId);
  }

  loadEnclosureToView(enclosureId: number) {
    this.enclosureService.getById(enclosureId).subscribe(
      (enclosure: Enclosure) => {
        this.enclosureToView = enclosure;
        console.log('Loaded enclosure id: ' + enclosure.id);
        if (this.enclosureToView == null) {
          this.error = true;
          this.errorMessage = 'Enclosure with such id does not exist.';
        }
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );

  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isUser(): boolean {
    return this.authService.getUserRole() === 'USER';
  }


  backClicked() {
    this._location.back();
  }


  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }
}
