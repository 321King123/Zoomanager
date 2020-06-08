import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Animal} from '../dtos/animal';
import {HttpClient} from '@angular/common/http';
import {Globals, Utilities} from '../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Injectable({
  providedIn: 'root'
})
export class AnimalService {

  private animalBaseUri: string = this.globals.backendUri + '/animals';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createAnimal(animal: Animal): Observable<Animal> {
    DEBUG_LOG('Create animal:' + JSON.stringify(animal));
    return this.httpClient.post<Animal>(this.animalBaseUri, animal);
  }

  /**
   * Get List of all current animals
   */
  getAnimals(): Observable<Animal[]> {
    DEBUG_LOG('Getting all animals');
    return this.httpClient.get<Animal[]>(this.animalBaseUri);
  }

  deleteAnimal(animal: Animal) {
    DEBUG_LOG('Delete animal: ' + JSON.stringify(animal));
    return this.httpClient.delete(this.animalBaseUri + '/' + animal.id);
  }

  getAnimalById(id): Observable<Animal> {
    DEBUG_LOG('Get animal by id: ' + id);
    return this.httpClient.get<Animal>(this.animalBaseUri + '/' + id);
  }

  updateAnimal(animal: Animal) {
    DEBUG_LOG('Update animal: ' + JSON.stringify(animal));
    return this.httpClient.put(this.animalBaseUri + '/edit', animal);
  }
}
