import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EmployeeService} from '../../services/employee.service';
import {AuthService} from '../../services/auth.service';
import {Employee} from '../../dtos/employee';
import {type} from '../../global/globals';
import {Animal} from '../../dtos/animal';
import {AnimalService} from '../../services/animal.service';
import {Router } from '@angular/router';



@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.css']
})
export class EmployeeComponent implements OnInit {

  error: boolean = false;
  errorMessage: string = '';

  employeeCreationForm: FormGroup;

  searchEmployee = new Employee(null, null, null, '', null, null);

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
              private authService: AuthService, private route: Router ) {
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
    this.getAllEmployees();
    this.getAllAnimals();
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
   * Get All current employees
   */
  getAllEmployees() {
    this.employeeService.getAllEmployees().subscribe(
      employees => {
        this.employeeList = employees;
      },
      error => {
        console.log('Failed to load all employees');
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

  /**
   * Selects an employee from the table to display assigned animals
   */
  selectEmployee(employee: Employee) {
    this.selectedEmployee = null;
    this.assignedAnimals = null;
    if (employee.type === this.employeeTypes.ANIMAL_CARE) {
      this.selectedEmployee = employee;
      this.employeeService.getAnimals(employee).subscribe(
        animals => {
          this.assignedAnimals = animals;
        },
        error => {
          console.log('Failed to load animals of ' + this.selectedEmployee.username);
          this.defaultServiceErrorHandling(error);
        }
      );
    }
  }

  /**
   * Get All current animals
   */
  getAllAnimals() {
    this.animalService.getAnimals().subscribe(
      animals => {
        this.animalList = animals;
      },
      error => {
        console.log('Failed to load animals');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Assigns animal to the selected employee
   */
  assignAnimal() {
    this.employeeService.assignAnimalToEmployee(this.selectedAnimal, this.selectedEmployee).subscribe(
      () => {},
      error => {
        console.log('Failed to assign animal');
        this.defaultServiceErrorHandling(error);
      }
    );
    this.selectEmployee(this.selectedEmployee);
  }

  showInfo(e: Employee) {
    this.route.navigate(['/employee-view/' + e.username ]);
  }
}
