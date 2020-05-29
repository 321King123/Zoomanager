import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureService} from '../../services/enclosure.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {Animal} from '../../dtos/animal';
import {AnimalService} from '../../services/animal.service';
import {EnclosureTask} from '../../dtos/enclosureTask';
import {TaskService} from '../../services/task.service';
import {Employee} from '../../dtos/employee';
import {EmployeeService} from '../../services/employee.service';
import {Task} from '../../dtos/task';

@Component({
  selector: 'app-enclosure-view',
  templateUrl: './enclosure-view.component.html',
  styleUrls: ['./enclosure-view.component.css']
})
export class EnclosureViewComponent implements OnInit {

  error: boolean = false;
  errorMessage: string = '';

  enclosureToView: Enclosure;
  selectedAnimal: Animal = null;
  assignedAnimals: Animal[];
  alreadyAssignedEnclosureOfSelectedAnimal: Enclosure;
  animalList: Animal[];
  tasks: Task[];
  employeesAssigned: Employee[];
  janitors: Employee[];


  constructor(private enclosureService: EnclosureService, private authService: AuthService,
              private route: ActivatedRoute, private router: Router, private _location: Location,
              private animalService: AnimalService, private taskService: TaskService,
              private employeeService: EmployeeService) {

  }

  ngOnInit(): void {
    const enclsureToViewId = Number(this.route.snapshot.paramMap.get('enclosureId'));
    this.loadAnimals();
    this.loadEnclosureToView(enclsureToViewId);
  }

  loadAnimals() {
    this.animalService.getAnimals().subscribe(
      animals => {
        this.animalList = animals;
      },
      error => {
        if (error.status === 404) {
          this.animalList.length = 0;
        }
        console.log('Failed to load all animals');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  loadEnclosureTasks() {
    this.taskService.getTasksOfEnclosure(this.enclosureToView.id).subscribe(
      (tasks) => {
        this.tasks = tasks;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  loadEnclosureToView(enclosureId: number) {
    this.enclosureService.getById(enclosureId).subscribe(
      (enclosure: Enclosure) => {
        this.enclosureToView = enclosure;
        console.log('Loaded enclosure id: ' + enclosure.id);
        if (this.enclosureToView == null) {
          this.error = true;
          this.errorMessage = 'Enclosure with such id does not exist.';
        }
        this.showAssignedAnimalsEnclosure();
        this.loadEnclosureTasks();
        this.loadEmployees();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );

  }

  loadEmployees() {
    this.employeeService.getJanitors().subscribe(
      janitors => {
        this.janitors = janitors;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
    this.employeeService.getEmployeesOfEnclosure(this.enclosureToView.id).subscribe(
      employees => {
        this.employeesAssigned = employees;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isUser(): boolean {
    return this.authService.getUserRole() === 'USER';
  }


  backClicked() {
    this._location.back();
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

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  /**
   * Selects an employee from the table to display assigned animals
   */
  showAssignedAnimalsEnclosure() {
    if (this.enclosureToView !== null) {
      this.enclosureService.getAssignedAnimals(this.enclosureToView).subscribe(
        animals => {
          this.assignedAnimals = animals;
        },
        error => {
          console.log('Failed to load animals of ' + this.enclosureToView.id);
          this.defaultServiceErrorHandling(error);
        }
      );
    }
  }

  assignAnimaltoEnclosure() {
    if (this.assignedAnimals !== undefined) {
      for (let i = 0; i < this.assignedAnimals.length; i++) {
        if (this.assignedAnimals[i].id === this.selectedAnimal.id) {
          this.error = true;
          this.errorMessage = 'This animal is already assigned to ' + this.enclosureToView.id;
          return;
        }
      }
    }
    this.enclosureService.getAlreadyAssignedEnclosureToAnimal(this.selectedAnimal).subscribe(
      (enclosure) => {
        this.alreadyAssignedEnclosureOfSelectedAnimal = enclosure;
        if (this.alreadyAssignedEnclosureOfSelectedAnimal !== null) {
          if (confirm('Animal is already assigned to enclosure with id: ' + this.alreadyAssignedEnclosureOfSelectedAnimal.id + '. Do you want to move ' + this.selectedAnimal.name + ' into this enclosure')) {
            console.log('assigning ' + this.selectedAnimal + ' to ' + this.enclosureToView);
            this.enclosureService.assignAnimalToEnclosure(this.selectedAnimal, this.enclosureToView).subscribe(
              () => {
                this.showAssignedAnimalsEnclosure();
              },
              error => {
                console.log('Failed to assign animal');
                this.defaultServiceErrorHandling(error);
              }
            );
          }
        } else {
          console.log('assigning ' + this.selectedAnimal + ' to ' + this.enclosureToView);
          this.enclosureService.assignAnimalToEnclosure(this.selectedAnimal, this.enclosureToView).subscribe(
            () => {
              this.showAssignedAnimalsEnclosure();
            },
            error => {
              console.log('Failed to assign animal');
              this.defaultServiceErrorHandling(error);
            }
          );
        }
      },
      error => {
        console.log('Failed to get enclosure where animal assigned');
        this.defaultServiceErrorHandling(error);
      }
    );

  }

  deleteEnclosure() {
    this.enclosureService.deleteEnclosure(this.enclosureToView).subscribe(
      () => {
        console.log('Deleted enclosure:' + this.enclosureToView.id);
        this.backClicked();
      },
      error => {
        console.log('Failed to delete enclosure');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  unassignAnimal(animal: Animal) {
    if (animal != null) {
      this.enclosureService.unassignAnimal(animal).subscribe(
        () => {
          console.log('Removed animal' + animal);
          this.showAssignedAnimalsEnclosure();
        },
        error => {
          console.log('Failed to remove animal');
          this.defaultServiceErrorHandling(error);
        }
      );
    }

  }
}
