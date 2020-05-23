import {Component, OnInit} from '@angular/core';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {TaskService} from '../../services/task.service';
import {Animal} from '../../dtos/animal';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-animal-view',
  templateUrl: './animal-view.component.html',
  styleUrls: ['./animal-view.component.css']
})
export class AnimalViewComponent implements OnInit {
  error = false;
  errorMessage = '';
  currentAnimal: Animal;

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
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getTasksOfAnimal() {

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
}
