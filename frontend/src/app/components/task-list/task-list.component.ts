import {Component, OnInit} from '@angular/core';
import {Input} from '@angular/core';
import {AnimalTask} from '../../dtos/animalTask';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {
  @Input() tasks: AnimalTask;

  constructor() {
  }

  ngOnInit(): void {
  }

  reassign() {

  }
}
