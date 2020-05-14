import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {FormBuilder} from '@angular/forms';
import {AnimalService} from '../../services/animal.service';

@Component({
  selector: 'app-animal',
  templateUrl: './animal.component.html',
  styleUrls: ['./animal.component.css']
})
export class AnimalComponent implements OnInit {
  error: boolean = false;
  errorMessage: string = '';
  animalCreationForm: any;
  submittedAnimal: any;


  constructor(private animalService: AnimalService, private formBuilder: FormBuilder, private authService: AuthService) { }

  ngOnInit(): void {
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

  }
}
