import { Component, OnInit } from '@angular/core';
import {EmployeeService} from '../../services/employee.service';
import {ActivatedRoute} from '@angular/router';
import {Employee} from '../../dtos/employee';
import {AuthService} from '../../services/auth.service';
import {Location} from '@angular/common';

@Component({
  selector: 'app-employee-view',
  templateUrl: './employee-view.component.html',
  styleUrls: ['./employee-view.component.css']
})
export class EmployeeViewComponent implements OnInit {

  public  employee: Employee;
  error: boolean = false;
  errorMessage: string = '';
  currentUser: string;
  constructor(private employeeService: EmployeeService, private authService: AuthService, private route: ActivatedRoute, private _location: Location) { }

  ngOnInit(): void {
    this.currentUser = (this.route.snapshot.paramMap.get('username'));
    this.loadSpecificEmployee(this.currentUser);
  }

  loadSpecificEmployee(username: string) {

    this.employeeService.getEmployeeByUsername(username).subscribe(
      (employee: Employee) => {
        this.employee = employee;
        console.log('employee: ' + this.employee);
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
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
  backClicked() {
    this._location.back();
  }
  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Displays a Date Object as yyyy-mm-dd
   */
  displayDate(date: Date): string {
    let dateString: string;
    dateString = String(date).substring(0, 10);
    return dateString;
  }

}
