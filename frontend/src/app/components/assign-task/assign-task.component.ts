import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Employee} from '../../dtos/employee';
import {AnimalTask} from '../../dtos/animalTask';
import {TaskService} from '../../services/task.service';
import {Animal} from '../../dtos/animal';
import {EnclosureTask} from '../../dtos/enclosureTask';

@Component({
  selector: 'app-assign-task',
  templateUrl: './assign-task.component.html',
  styleUrls: ['./assign-task.component.css']
})
export class AssignTaskComponent implements OnInit {
  @Input() enclosureTask: EnclosureTask;
  @Input() task: AnimalTask;
  @Input() employees: Employee[];
  @Input() doctors: Employee [];
  @Input() janitors: Employee[];
  @Input() index: number;
  @Output() assignmentSuccessful = new EventEmitter();

  enable = true;
  selectedEmployee: Employee;

  error = false;
  errorMessage = '';

  success = false;
  lastAssignmentSuccessful = false;

  uniqId;

  constructor(private taskService: TaskService) {
  }

  ngOnInit(): void {
    if (this.enclosureTask) {
      this.uniqId = 'enclosure';
    }
    if (this.task) {
      this.uniqId = 'animal';
    }
  }

  assign() {
    this.taskService.assignTask(this.task.id, this.selectedEmployee).subscribe(
      (res: any) => {
        this.success = true;
        this.enable = false;
        this.lastAssignmentSuccessful = true;
      },
      error => {
        this.defaultServiceErrorHandling(error);
        this.lastAssignmentSuccessful = false;
      }
    );
  }

  vanishError() {
    this.error = false;
  }

  vanishSuccess() {
    this.success = true;
  }

  vanishAll() {
    this.vanishError();
    this.vanishSuccess();
    this.selectedEmployee = null;
    if (this.lastAssignmentSuccessful) {
      this.assignmentSuccessful.emit();
    }
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
