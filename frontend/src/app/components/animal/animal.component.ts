import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AnimalService} from '../../services/animal.service';
import {Animal} from '../../dtos/animal';

@Component({
  selector: 'app-animal',
  templateUrl: './animal.component.html',
  styleUrls: ['./animal.component.css']
})
export class AnimalComponent implements OnInit {
  error: boolean = false;
  errorMessage: string = '';
  animalCreationForm: FormGroup;
  submittedAnimal = false;
  animals: Animal[];

  constructor(private animalService: AnimalService, private formBuilder: FormBuilder, private authService: AuthService) {
    this.animalCreationForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      species: ['', [Validators.required]],
      publicInformation: [''],
      description: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.getAnimals();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }


  addAnimal() {
    this.submittedAnimal = true;
    if (this.animalCreationForm.valid) {
      const animal: Animal = new Animal(
        null,
        this.animalCreationForm.controls.name.value,
        this.animalCreationForm.controls.description.value,
        this.animalCreationForm.controls.species.value,
        null,
        this.animalCreationForm.controls.publicInformation.value);
      this.createAnimal(animal);
      this.clearForm();
    } else {
      console.log('Invalid input.');
    }

  }

  createAnimal(animal: Animal) {
    this.animalService.createAnimal(animal).subscribe(
      () => {
        this.getAnimals();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Get All current animals
   */
  getAnimals() {
    this.animalService.getAnimals().subscribe(
      animals => {
        this.animals = animals;
      },
      error => {
        console.log('Failed to load all animals');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  deleteAnimal(animal: Animal) {
    this.animalService.deleteAnimal(animal).subscribe(
      (res: any) => {
        this.getAnimals();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private clearForm() {
    this.animalCreationForm.reset();
    this.submittedAnimal = false;
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
