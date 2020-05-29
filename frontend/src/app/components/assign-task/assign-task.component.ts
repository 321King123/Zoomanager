import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Employee} from '../../dtos/employee';
import {AnimalTask} from '../../dtos/animalTask';
import {TaskService} from '../../services/task.service';
import {Animal} from '../../dtos/animal';
import {EnclosureTask} from '../../dtos/enclosureTask';
import {Task} from '../../dtos/task';
import {AlertService} from '../../services/alert.service';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
@Component({
  selector: 'app-assign-task',
  templateUrl: './assign-task.component.html',
  styleUrls: ['./assign-task.component.css']
})
export class AssignTaskComponent implements OnInit {
  @Input() task: Task;
  @Input() employees: Employee[];
  @Input() doctors: Employee [];
  @Input() janitors: Employee[];
  @Input() index: number;
  @Output() assignmentSuccessful = new EventEmitter();

  componentId = 'assign-task';

  enable = true;
  selectedEmployee: Employee;

  // error = false;
  // errorMessage = '';
  //
  // success = false;
  // lastAssignmentSuccessful = false;

  uniqId;

  constructor(private taskService: TaskService, private alertService: AlertService) {
  }

  ngOnInit(): void {
  }



  assign() {
    this.taskService.assignTask(this.task.id, this.selectedEmployee).subscribe(
      (res: any) => {
        this.enable = false;
        this.alertService.success('Task was successfully assigned!',
          {componentId: this.componentId, title: 'Success!'},
          'assign-task assign');
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'assign-task assign');
      }
    );
  }

  vanishAll() {
    DEBUG_LOG('VANISH ALL');
    this.selectedEmployee = null;
    this.alertService.clear(this.componentId);
  }
}
