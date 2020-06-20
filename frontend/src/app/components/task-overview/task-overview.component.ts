import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Task} from '../../dtos/task';
import {TaskService} from '../../services/task.service';
import {type, Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {AlertService} from '../../services/alert.service';
import {EmployeeService} from '../../services/employee.service';
import {Employee} from '../../dtos/employee';

@Component({
  selector: 'app-task-overview',
  templateUrl: './task-overview.component.html',
  styleUrls: ['./task-overview.component.css']
})
export class TaskOverviewComponent implements OnInit {

  tasks: Task[];
  filterTask: Task;
  employeeList: Employee[];
  employeeType: type;
  openFilter: boolean;
  employeeTypes = type;
  typeValues = [];
  statusValues = ['NOT_ASSIGNED', 'ASSIGNED', 'DONE'];
  userType;

  constructor(private authService: AuthService, private taskService: TaskService,
              private employeeService: EmployeeService, private alertService: AlertService) {
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
        this.alertService.alertFromError(error,  {}, 'loadTasksOfEmployee->loadTaskOfEmployee');
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
