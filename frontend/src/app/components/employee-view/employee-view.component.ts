import {Component, OnInit} from '@angular/core';
import {EmployeeService} from '../../services/employee.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Employee} from '../../dtos/employee';
import {AuthService} from '../../services/auth.service';
import {Location} from '@angular/common';
import {Animal} from '../../dtos/animal';
import {AnimalService} from '../../services/animal.service';

@Component({
  selector: 'app-employee-view',
  templateUrl: './employee-view.component.html',
  styleUrls: ['./employee-view.component.css']
})
export class EmployeeViewComponent implements OnInit {

  public employee: Employee;
  error: boolean = false;
  errorMessage: string = '';
  currentUser: string;
  check: string;
  animalList: Animal[];
  selectedAnimal: Animal = null;
  assignedAnimals: Animal[];

  constructor(private employeeService: EmployeeService, private authService: AuthService, private route: ActivatedRoute,
              private _location: Location, private animalService: AnimalService, private router: Router) {
  }

  ngOnInit(): void {
    this.currentUser = (this.route.snapshot.paramMap.get('username'));
    if (this.isAdmin() && this.currentUser != null) {
      this.loadSpecificEmployee(this.currentUser);
      if (this.employee == null) {
        this.error = true;
        this.errorMessage = 'Employee with such username does not exist.';
      } else {
        this.getAllAnimals();
      }
    } else if (this.currentUser == null) {
      this.loadPersonalInfo();
    } else {
      this.error = true;
      this.errorMessage = 'You are NOT authorised to see this users information!';
    }

  }

  loadPersonalInfo() {
    this.employeeService.getPersonalInfo().subscribe(
      (employee: Employee) => {
        this.employee = employee;
        console.log('employee: ' + JSON.stringify(this.employee));
        this.showAssignedAnimalsEmployee();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  loadSpecificEmployee(username: string) {

    this.employeeService.getEmployeeByUsername(username).subscribe(
      (employee: Employee) => {
        this.employee = employee;
        console.log('employee: ' + JSON.stringify(this.employee));
        this.showAssignedAnimalsEmployee();
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

  /**
   * Selects an employee from the table to display assigned animals
   */
  showAssignedAnimalsEmployee() {
    if (this.employee !== null && this.employee.type === 'ANIMAL_CARE') {
      this.employeeService.getAnimals(this.employee).subscribe(
        animals => {
          this.assignedAnimals = animals;
        },
        error => {
          console.log('Failed to load animals of ' + this.employee.username);
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
    if (this.assignedAnimals !== undefined) {
      for (let i = 0; i < this.assignedAnimals.length; i++) {
        if (this.assignedAnimals[i].id === this.selectedAnimal.id) {
          this.error = true;
          this.errorMessage = 'This animal is already assigned to ' + this.employee.username;
          return;
        }
      }
      console.log('assigning ' + this.selectedAnimal + ' to ' + this.employee);
    }
    this.employeeService.assignAnimalToEmployee(this.selectedAnimal, this.employee).subscribe(
      () => {
        this.showAssignedAnimalsEmployee();
      },
      error => {
        console.log('Failed to assign animal');
        this.defaultServiceErrorHandling(error);
      }
    );
  }


}
