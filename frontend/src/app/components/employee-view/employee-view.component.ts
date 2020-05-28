import {Component, OnInit} from '@angular/core';
import {EmployeeService} from '../../services/employee.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Employee} from '../../dtos/employee';
import {AuthService} from '../../services/auth.service';
import {Location} from '@angular/common';
import {Animal} from '../../dtos/animal';
import {AnimalService} from '../../services/animal.service';
import {AnimalTask} from '../../dtos/animalTask';
import {TaskService} from '../../services/task.service';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureService} from '../../services/enclosure.service';
import {EnclosureTask} from '../../dtos/enclosureTask';
import {AlertService} from '../../services/alert.service';
import {Alert, AlertType} from '../../dtos/alert';

@Component({
  selector: 'app-employee-view',
  templateUrl: './employee-view.component.html',
  styleUrls: ['./employee-view.component.css']
})
export class EmployeeViewComponent implements OnInit {

  public employee: Employee;
  currentUserType;

  error: boolean = false;
  errorMessage: string = '';
  currentUser: string;
  check: string;
  animalList: Animal[];
  selectedAnimal: Animal = null;
  assignedAnimals: Animal[];
  tasks: AnimalTask[];
  enclosureTasks: EnclosureTask[];

  enclosuresFound = false;
  enclosuresOfEmployee: Enclosure[];

  taskListMode: boolean;
  animalListMode: boolean;

  constructor(private employeeService: EmployeeService, private authService: AuthService, private route: ActivatedRoute,
              private _location: Location, private animalService: AnimalService, private router: Router,
              private taskService: TaskService, private enclosureService: EnclosureService,
              private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.currentUser = (this.route.snapshot.paramMap.get('username'));
    if (this.isAdmin() && this.currentUser != null) {
      this.loadSpecificEmployee(this.currentUser);
      this.getAllAnimals();
    } else if (this.currentUser == null) {
      this.loadPersonalInfo();
    } else {
      this.error = true;
      this.errorMessage = 'You are NOT authorised to see this users information!';
    }

  }

  getCurrentUserType() {
    if (this.isAdmin()) {
      return 'ADMIN';
    } else  {
      return this.employee.type;
    }
  }

  loadPersonalInfo() {
    this.employeeService.getPersonalInfo().subscribe(
      (employee: Employee) => {
        this.employee = employee;
        console.log('employee: ' + JSON.stringify(this.employee));
        this.showAssignedAnimalsEmployee();
        this.loadTasksOfEmployee();
        this.currentUserType = this.getCurrentUserType();
        this.toAnimalMode();
        this.getEnclosuresOfEmployee();
      },
      error => {
        this.alertService.alertFromError(error,  {}, 'loadPersonInfo');
      }
    );
  }

  loadSpecificEmployee(username: string) {

    this.employeeService.getEmployeeByUsername(username).subscribe(
      (employee: Employee) => {
        this.employee = employee;
        if (this.employee == null) {
          this.error = true;
          this.errorMessage = 'Employee with such username does not exist.';
        } else {
          console.log('Loaded Employee: ' + this.employee.username);
          this.showAssignedAnimalsEmployee();
          this.loadTasksOfEmployee();
          this.toAnimalMode();
          this.getEnclosuresOfEmployee();
        }
      },
      error => {
        this.alertService.alertFromError(error,  {}, 'loadSpecificEmployee');
      }
    );
  }

  getEnclosuresOfEmployee() {
    this.enclosuresFound = false;
    this.enclosureService.getEnclosuresOfEmployee(this.employee.username).subscribe(
      (enclosures) => {
        this.enclosuresOfEmployee = enclosures;
        this.enclosuresFound = true;
      },
      error => {
        this.alertService.alertFromError(error,  {}, 'getEnclosureOfEmployee');
      }
    );
  }

  loadTasksOfEmployee() {
    this.taskService.getAnimalTasksOfEmployee(this.employee.username).subscribe(
      (tasks) => {
        this.tasks = tasks;
      },
      error => {
        console.log('Error loading tasks!');
        this.alertService.alertFromError(error,  {}, 'loadTasksOfEmployee->loadTaskOfEmployee');
      }
    );
    this.taskService.getEnclosureTasksOfEmployee(this.employee.username).subscribe(
      (enclosureTasks) => {
        this.enclosureTasks = enclosureTasks;
      },
      error => {
        this.alertService.alertFromError(error,  {}, 'loadTasksOfEmployee->getEnclosureTasksOfEmployee');
      }
    );
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      const message: string = error.error.error;
      const type = AlertType.Error;
      this.alertService.alert(new Alert({message, type}), 'defaultServiceErrorHandling');
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
          this.alertService.alertFromError(error, {}, 'showAssignedAnimalsEmployee');
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
        const options: any = {
          keepAfterRouteChange: true,
          dismissible: true,
        };
        this.alertService.alertFromError(error,  {keepAfterRouteChange: true},
          'getAllAnimals');
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
        this.alertService.alertFromError(error, {}, 'assignAnimal');
      }
    );
  }

  deleteEmployee() {
    if (this.assignedAnimals !== undefined && this.assignedAnimals.length !== 0) {
      if (confirm('Employee still has animals assigned, are you sure you want to delete?')) {
        this.employeeService.deleteEmployee(this.employee.username).subscribe(
          () => {
            this.backClicked();
          },
          error => {
            console.log('Failed to delete employee');
            this.alertService.alertFromError(error, {}, 'deleteEmployee');
          }
        );
      }
    } else {
      this.employeeService.deleteEmployee(this.employee.username).subscribe(
        () => {
          this._location.back();
        },
        error => {
          console.log('Failed to delete employee');
          this.alertService.alertFromError(error, {}, 'deleteEmployee');
        }
      );
    }
  }

  toTaskMode() {
    this.animalListMode = false;
    this.taskListMode = true;
  }

  toAnimalMode() {
    this.animalListMode = true;
    this.taskListMode = false;
  }
}
