import {Component, OnInit} from '@angular/core';
import {Input} from '@angular/core';
import {AnimalTask} from '../../dtos/animalTask';
import {Employee} from '../../dtos/employee';
import {TaskService} from '../../services/task.service';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {
  @Input() tasks: AnimalTask[];

  constructor(private taskService: TaskService, private animalService: AnimalService,
              private employeeService: EmployeeService) {
  }

  ngOnInit(): void {
  }

  reassign() {

  }
}
