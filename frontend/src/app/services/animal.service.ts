import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Animal} from '../dtos/animal';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class AnimalService {

  private animalBaseUri: string = this.globals.backendUri + '/animals';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createAnimal(animal: Animal): Observable<Animal> {
    console.log('Create animal:' + JSON.stringify(animal));
    return this.httpClient.post<Animal>(this.animalBaseUri, animal);
  }

  /**
   * Get List of all current animals
   */
  getAnimals(): Observable<Animal[]> {
    console.log('Getting all animals');
    return this.httpClient.get<Animal[]>(this.animalBaseUri);
  }

  deleteAnimal(animal: Animal) {
    console.log('Delete animal: ' + JSON.stringify(animal));
    return this.httpClient.delete(this.animalBaseUri + '/' + animal.id);
  }
}
