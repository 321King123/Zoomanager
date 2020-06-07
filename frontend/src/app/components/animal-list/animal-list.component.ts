import {Component, OnInit, Input, Output, EventEmitter, ViewChildren, QueryList} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Animal} from '../../dtos/animal';
import {AnimalService} from '../../services/animal.service';
import {Router} from '@angular/router';
import {DeleteWarningComponent} from '../delete-warning/delete-warning.component';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {AlertService} from '../../services/alert.service';

@Component({
  selector: 'app-animal-list',
  templateUrl: './animal-list.component.html',
  styleUrls: ['./animal-list.component.css']
})
export class AnimalListComponent implements OnInit {
  @Input('animals') animals: Animal[];
  enableDelete: boolean = false;
  @Input() animalPage;
  @Output() deleteAnimal = new EventEmitter<Animal>();
  @Input() enclosurePage;
  @Output() unassignAnimal = new EventEmitter<Animal>();

  @ViewChildren(DeleteWarningComponent)
  deleteWarningComponents: QueryList<DeleteWarningComponent>;
  stopClickPropagation: boolean = false;

  constructor(private authService: AuthService, private animalService: AnimalService, private route: Router,
              private alertService: AlertService) {
  }

  ngOnInit(): void {
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  changeDeleteState() {
    this.enableDelete = !this.enableDelete;
  }

  showInfo(a: Animal) {
    if (!this.stopClickPropagation) {
      console.log('got here');
      this.route.navigate(['/animal-view/' + a.id]);
    }
  }

  deleteAnimalFn(animal: any) {
    this.deleteAnimal.emit(animal);
  }

  delAnimal(animal: any) {
    this.animalService.deleteAnimal(animal).subscribe(
      () => {
        this.loadAllAnimals();
      },
      error => {
        this.alertService.alertFromError(error, {}, 'animal-list deleteAnimal(' + JSON.stringify(animal) + ')');
      }
    );
  }

  loadAllAnimals() {
    this.animalService.getAnimals().subscribe(
      (animals) => {
        if (animals.length > 0) {
          this.animals = animals;
        } else {
          this.animals.length = 0;
        }

      },
    error => {
        if (error.status !== undefined && error.status === 404) {
          this.animals.length = 0;
        }
      this.alertService.alertFromError(error, {}, 'animal-list loadAnimals()');
    }
    );
  }

  toggleClickPropagation () {
    DEBUG_LOG('Before Toggled CLICK propagation: ' + this.stopClickPropagation);
    this.stopClickPropagation = !this.stopClickPropagation;
    DEBUG_LOG('Toggled CLICK click propagation: ' + this.stopClickPropagation);
  }
}

