import {Component, OnInit} from '@angular/core';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {TaskService} from '../../services/task.service';
import {Animal} from '../../dtos/animal';
import {ActivatedRoute, Router} from '@angular/router';
import {Employee} from '../../dtos/employee';
import {AnimalTask} from '../../dtos/animalTask';

@Component({
  selector: 'app-animal-view',
  templateUrl: './animal-view.component.html',
  styleUrls: ['./animal-view.component.css']
})
export class AnimalViewComponent implements OnInit {
  error = false;
  errorMessage = '';
  currentAnimal: Animal;

  doctors: Employee[];
  employees: Employee[];
  tasks: AnimalTask[];

  constructor(private animalService: AnimalService, private employeeService: EmployeeService,
              private taskService: TaskService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    const currentAnimalId = (this.route.snapshot.paramMap.get('animalId'));
    this.getCurrentAnimal(currentAnimalId);
  }

  getCurrentAnimal(id) {
    this.animalService.getAnimalById(id).subscribe(
      (a: Animal) => {
        this.currentAnimal = a;
        this.getEmployeesOfAnimal();
        this.getTasksOfAnimal();
        this.getDoctors();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getTasksOfAnimal() {
    this.taskService.getTasksOfAnimal(this.currentAnimal.id).subscribe(
      (tasks: AnimalTask[]) => {
        this.tasks = tasks;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
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

  vanishError() {
    this.error = false;
  }

  getDoctors() {
    this.employeeService.getDoctors().subscribe(
      (doctors) => {
        this.doctors = doctors;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getEmployeesOfAnimal() {
    this.employeeService.getEmployeesOfAnimal(this.currentAnimal.id).subscribe(
      (employees) => {
        this.employees = employees;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }
}
