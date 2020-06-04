import {Component, OnInit, Output, EventEmitter, Input} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {TaskService} from '../../services/task.service';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {Employee} from '../../dtos/employee';
import {AnimalTask} from '../../dtos/animalTask';
import {Task} from '../../dtos/task';
import {AlertService} from '../../services/alert.service';

@Component({
  selector: 'app-task-list-common',
  templateUrl: './task-list-common.component.html',
  styleUrls: ['./task-list-common.component.css']
})
export class TaskListCommonComponent implements OnInit {
  componentId = 'task-list-common';

  @Input() tasks: Task[];

  @Input() doctors: Employee[];
  @Input() janitors: Employee[];
  @Input() employees: Employee[];

  @Output() reloadTasks = new EventEmitter();

  @Input() currentUserType;
  currentUser: Employee;


  constructor(private authService: AuthService, private taskService: TaskService, private animalService: AnimalService,
              private employeeService: EmployeeService, private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.getCurrentUser();
  }

  markTaskAsDone(taskId) {
    this.taskService.markTaskAsDone(taskId).subscribe(
      (res: any) => {
        this.reloadTasks.emit();
      },
      error => {
        this.alertService.alertFromError(error, { componentId: this.componentId}, 'TaskList component: markTaskAsDone');
      }
    );
  }

  deleteTask(taskId) {
    this.taskService.deleteTask(taskId).subscribe(
      () => {
        this.reloadTasks.emit();
      },
      error => {
        this.alertService.alertFromError(error, { componentId: this.componentId}, 'TaskList component: markTaskAsDone');
      }
    );
  }

  getCurrentUser() {
    if (!this.isAdmin()) {
      this.employeeService.getPersonalInfo().subscribe(
        (emp: Employee) => {
          this.currentUser = emp;
        }
      );
    }
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

}
