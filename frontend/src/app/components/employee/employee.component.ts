import {Component, Injectable, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EmployeeService} from '../../services/employee.service';
import {AuthService} from '../../services/auth.service';
import {Employee} from '../../dtos/employee';
import {NgbTimeStringAdapter, type} from '../../global/globals';
import {Animal} from '../../dtos/animal';
import {AnimalService} from '../../services/animal.service';
import {Router } from '@angular/router';
import {Time} from '@angular/common';
import {AlertService} from '../../services/alert.service';
import {AlertType} from '../../dtos/alert';
import {NgbTimeAdapter, NgbTimeStruct} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.css'],
  providers: [{provide: NgbTimeAdapter, useClass: NgbTimeStringAdapter}]
})
export class EmployeeComponent implements OnInit {
  componentId: string;

  error: boolean = false;
  errorMessage: string = '';

  employeeCreationForm: FormGroup;

  searchEmployee = new Employee(null, null, null, '', null, null, null, null);

  submittedEmployee: boolean = false;

  employeeTypes = type;

  types = type;

  typeValues = [];

  employeeList: Employee[];

  selectedEmployee: Employee;

  animalList: Animal[];

  selectedAnimal: Animal = null;

  assignedAnimals: Animal[];

  constructor(private employeeService: EmployeeService, private animalService: AnimalService, private formBuilder: FormBuilder,
              private authService: AuthService, private route: Router, private alertService: AlertService) {
    this.typeValues = Object.keys(type);
    for (const t of this.typeValues) {
      console.log(t);
    }
    this.employeeCreationForm = this.formBuilder.group({
      username: ['', [Validators.required] ],
      email: ['', Validators.email],
      password: ['', Validators.compose([
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$')
      ])],
      name: ['', [Validators.required] ],
      birthday: ['', [Validators.required] ],
      workTimeStart: ['', [Validators.required]],
      workTimeEnd: ['', [Validators.required]],
      employeeType: ['', [Validators.required] ]
      // employeeType: new FormArray([], Validators.required)
    });
  }

  ngOnInit(): void {
    this.componentId = 'employees-overview';
    this.getAllEmployees();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  addEmployee() {
    this.submittedEmployee = true;
    if (this.employeeCreationForm.valid) {
      const employee: Employee = new Employee(
        this.employeeCreationForm.controls.username.value,
        this.employeeCreationForm.controls.email.value,
        this.employeeCreationForm.controls.password.value,
        this.employeeCreationForm.controls.name.value,
        this.employeeCreationForm.controls.birthday.value,
        this.employeeCreationForm.controls.workTimeStart.value,
        this.employeeCreationForm.controls.workTimeEnd.value,
        this.employeeCreationForm.controls.employeeType.value
      );
      console.log('type: ' + this.employeeCreationForm.controls.employeeType.value);
      console.log('time: ' + this.employeeCreationForm.controls.workTimeStart.value);
      this.createEmployee(employee);
      this.clearForm();
    } else {
      console.log('Invalid Input');
    }


  }

  /**
   * Sends Employee creation request
   * @param employee the employee which should be created
   */
  createEmployee(employee: Employee) {
    this.employeeService.createEmployee(employee).subscribe(
      (res: any) => {
        this.getAllEmployees();
      },
      error => {
        this.alertService.alertFromError(error,  {componentId: this.componentId}, 'createEmployee');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Get All current employees
   */
  getAllEmployees() {
    this.employeeService.getAllEmployees().subscribe(
      employees => {
        this.employeeList = employees;
      },
      error => {
        console.log('Failed to load all employees');
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'getAllEmployees');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Get filtered list of current Employees
   * The filters are saved in searchEmployee
   * currently only Name as substring filter and
   * type as Type filter supported
   */
  getFilteredEmployees() {
    this.employeeService.searchEmployees(this.searchEmployee).subscribe(
      employees => {
        this.employeeList = employees;
      },
      error => {
        console.log('Failed to load all employees');
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'getFilteredEmployees');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Displays a Date Object as yyyy-mm-dd
   */
  displayDate(date: Date): string {
    let dateString: string;
    dateString = String(date).substring(0, 10);
    return dateString;
  }

  displayTime(time: Time): string {
    let timeString: string;
    timeString = String(time).substring(0, 5);
    return timeString;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
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

  private clearForm() {
    this.employeeCreationForm.reset();
    this.submittedEmployee = false;
  }

  showInfo(e: Employee) {
    this.route.navigate(['/employee-view/' + e.username ]);
  }
}
