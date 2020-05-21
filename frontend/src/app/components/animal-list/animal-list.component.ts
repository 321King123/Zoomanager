import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Animal} from '../../dtos/animal';
import {AnimalService} from '../../services/animal.service';

@Component({
  selector: 'app-animal-list',
  templateUrl: './animal-list.component.html',
  styleUrls: ['./animal-list.component.css']
})
export class AnimalListComponent implements OnInit {
  @Input('animals') animals: any[];
  enableDelete: boolean = false;
  @Input() animalPage;
  @Output() deleteAnimal = new EventEmitter<Animal>();


  constructor(private authService: AuthService, private animalService: AnimalService) {
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
}

