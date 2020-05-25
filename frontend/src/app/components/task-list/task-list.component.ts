import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Input} from '@angular/core';
import {AnimalTask} from '../../dtos/animalTask';
import {Employee} from '../../dtos/employee';
import {TaskService} from '../../services/task.service';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {Animal} from '../../dtos/animal';
import {EnclosureTask} from '../../dtos/enclosureTask';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {
  @Input() tasks: AnimalTask[];
  @Input() enclosureTasks: EnclosureTask[];

  @Input() doctors: Employee[];
  @Input() janitors: Employee[];
  @Input() employees: Employee[];

  @Output() reloadTasks = new EventEmitter();
  @Output() deleteTaskEvent = new EventEmitter();

  error = false;
  errorMessage = '';


  constructor(private taskService: TaskService, private animalService: AnimalService,
              private employeeService: EmployeeService) {
  }

  ngOnInit(): void {
  }

  deleteTask(animalTaskId) {
    this.taskService.deleteTask(animalTaskId).subscribe(
      () => {
        this.deleteTaskEvent.emit();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

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
}
