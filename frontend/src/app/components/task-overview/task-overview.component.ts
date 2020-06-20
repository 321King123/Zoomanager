import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Task} from '../../dtos/task';
import {TaskService} from '../../services/task.service';
import {type, Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {AlertService} from '../../services/alert.service';
import {EmployeeService} from '../../services/employee.service';
import {Employee} from '../../dtos/employee';
import {Animal} from '../../dtos/animal';
import {Enclosure} from '../../dtos/enclosure';
import {AnimalService} from '../../services/animal.service';
import {EnclosureService} from '../../services/enclosure.service';

@Component({
  selector: 'app-task-overview',
  templateUrl: './task-overview.component.html',
  styleUrls: ['./task-overview.component.css']
})
export class TaskOverviewComponent implements OnInit {

  tasks: Task[];
  filterTask: Task;
  employeeList: Employee[];
  allAnimals: Animal[];
  allEnclosures: Enclosure[];
  employeeType: type;
  openFilter: boolean;
  employeeTypes = type;
  typeValues = [];
  statusValues = ['NOT_ASSIGNED', 'ASSIGNED', 'DONE'];
  userType;

  constructor(private authService: AuthService, private taskService: TaskService,
              private employeeService: EmployeeService, private animalService: AnimalService,
              private enclosureService: EnclosureService, private alertService: AlertService) {
    this.typeValues = Object.keys(type);
  }

  ngOnInit(): void {
    this.openFilter = false;
    this.filterTask = new Task(null, null, null, null,
      null, null, null, null, null, null, null);
    this.employeeType = null;
    if (this.isAdmin()) {
      this.userType = 'ADMIN';
      this.getAllEmployees();
      this.loadFilteredTasks();
      this.getAnimals();
      this.getAllEnclosures();
    }
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  loadFilteredTasks() {
    this.taskService.searchTasks(this.employeeType, this.filterTask).subscribe(
      (tasks) => {
        this.tasks = tasks;
      },
      error => {
        DEBUG_LOG('Error loading tasks!');
        this.alertService.alertFromError(error,  {}, 'Task Overview component: loadFilteredTasks()');
      }
    );

  }

  getAnimals() {
    this.animalService.getAnimals().subscribe(
      animals => {
        this.allAnimals = animals;
      },
      error => {
        if (error.status === 404) {
          this.allAnimals.length = 0;
        }
        DEBUG_LOG('Failed to load all animals');
        this.alertService.alertFromError(error, {}, 'Task Overview component: getAnimals()');
      }
    );
  }

  getAllEnclosures() {
    this.enclosureService.getAllEnclosures().subscribe(
      enclosures => {
        this.allEnclosures = enclosures;
      },
      error => {
        if (error.status === 404) {
          this.allEnclosures.length = 0;
        }
        DEBUG_LOG('Failed to load all enclosures');
        this.alertService.alertFromError(error, {}, 'Task Overview component: getAllEnclosures()');
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
        DEBUG_LOG('Failed to load all employees');
        this.alertService.alertFromError(error, {}, 'getAllEmployees');
      }
    );
  }

  toggleFilter(){
    this.openFilter = !this.openFilter;
  }

}
