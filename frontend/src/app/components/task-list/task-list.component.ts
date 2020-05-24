import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Input} from '@angular/core';
import {AnimalTask} from '../../dtos/animalTask';
import {Employee} from '../../dtos/employee';
import {TaskService} from '../../services/task.service';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {Animal} from '../../dtos/animal';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {
  @Input() tasks: AnimalTask[];
  @Input() doctors: Employee[];
  @Input() employees: Employee[];
  @Input() animal: Animal;
  @Output() reloadTasks = new EventEmitter();
  @Output() deleteTask = new EventEmitter<AnimalTask>();


  constructor(private taskService: TaskService, private animalService: AnimalService,
              private employeeService: EmployeeService) {
  }

  ngOnInit(): void {
  }
}
