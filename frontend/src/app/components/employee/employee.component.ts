import { Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EmployeeService} from '../../services/employee.service';
import {AuthService} from '../../services/auth.service';
import {Employee} from '../../dtos/employee';
import {type} from '../../global/globals';


@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.css']
})
export class EmployeeComponent implements OnInit {

  error: boolean = false;
  errorMessage: string = '';

  employeeCreationForm: FormGroup;

  submittedEmployee: boolean = false;

  employeeTypes = type;

  types = type;

  typeValues = [];

  constructor(private employeeService: EmployeeService, private formBuilder: FormBuilder, private authService: AuthService) {
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
      employeeType: ['', [Validators.required] ]
       // employeeType: new FormArray([], Validators.required)
    });
  }

  ngOnInit(): void {

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
        this.employeeCreationForm.controls.employeeType.value
      );
      console.log('type: ' + this.employeeCreationForm.controls.employeeType.value);
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
      () => {
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
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

}
