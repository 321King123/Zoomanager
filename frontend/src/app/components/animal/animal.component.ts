import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AnimalService} from '../../services/animal.service';
import {Animal} from '../../dtos/animal';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureService} from '../../services/enclosure.service';
import {Employee} from '../../dtos/employee';
import {EmployeeService} from '../../services/employee.service';
import {Location} from '@angular/common';

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
  enclosureList: Enclosure[];
  selectedEnclosure: Enclosure;
  employeeList: Employee[];
  selectedEmployee: Employee;


  constructor(private _location: Location, private animalService: AnimalService, private formBuilder: FormBuilder, private authService: AuthService,
              private enclosureService: EnclosureService, private employeeService: EmployeeService) {
    this.animalCreationForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      species: ['', [Validators.required]],
      publicInformation: [''],
      description: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.getAnimals();
    this.getAllEnclosures();
    this.getAllEmployees();
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
      (createdAnimal) => {
        this.getAnimals();
        if (this.selectedEnclosure != null) {
          this.enclosureService.assignAnimalToEnclosure(createdAnimal, this.selectedEnclosure).subscribe(
            () => {
              this.selectedEnclosure = null;
            },
            error => {
              console.log('Failed to assign enclosure');
              this.defaultServiceErrorHandling(error);
            }
          );
        }
        if (this.selectedEmployee != null) {
          this.employeeService.assignAnimalToEmployee(createdAnimal, this.selectedEmployee).subscribe(
              () => {
                this.selectedEmployee = null;
              },
              error => {
                console.log('Failed to assign employee');
                this.defaultServiceErrorHandling(error);
              }
            );
        }

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
        if (error.status === 404) {
          this.animals.length = 0;
        }
        console.log('Failed to load all animals');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  deleteAnimal(animal: Animal) {
    this.animalService.deleteAnimal(animal).subscribe(
      (res: any) => {
        this.backClicked();
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

  backClicked() {
    this._location.back();
  }

  private getAllEnclosures() {
    this.enclosureService.getAllEnclosures().subscribe(
      enclosures => {
        this.enclosureList = enclosures;
      },
      error => {
        if (error.status === 404) {
          this.enclosureList.length = 0;
        }
        console.log('Failed to load all enclosures');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private getAllEmployees() {
    this.employeeService.getAllEmployees().subscribe(
      employees => {
        this.employeeList = employees;
      },
      error => {
        if (error.status === 404) {
          this.employeeList.length = 0;
        }
        console.log('Failed to load all employees');
        this.defaultServiceErrorHandling(error);
      }
    );
  }
}
