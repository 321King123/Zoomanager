import { Injectable } from '@angular/core';
import {Enclosure} from '../dtos/enclosure';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Employee} from '../dtos/employee';
import {Animal} from '../dtos/animal';

@Injectable({
  providedIn: 'root'
})
export class EnclosureService {

  private enclosureBaseUri: string = this.globals.backendUri + '/enclosure';
  private animalBaeseuri: string = this.globals.backendUri + 'animals';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  createEnclosure(enclosure: Enclosure): Observable<Enclosure> {
    console.log('Create Enclosure: ' + JSON.stringify(enclosure));
    return this.httpClient.post<Enclosure>(this.enclosureBaseUri, enclosure);
  }

  getAllEnclosures(): Observable<Enclosure[]> {
    console.log('Load all Enclosures.');
    return this.httpClient.get<Enclosure[]>(this.enclosureBaseUri);
  }

  getById(enclosureId: number): Observable<Enclosure> {
    console.log('Get Enclosure by id: ' + enclosureId);
    return this.httpClient.get<Enclosure>(this.enclosureBaseUri + '/' + enclosureId);
  }
  /**
   * Get all assigned animals of the enclosure
   * @param enclosure whose assigned animals will be returned
   */
  getAssignedAnimals(enclosure: Enclosure): Observable<Animal[]> {
    return this.httpClient.get<Animal[]>(this.animalBaeseuri + '/enclosure/' + enclosure.id);
  }

  /**
   * Assigns an animal to an enclosure
   * @param animal to be assigned
   * @param enclosure the animal will be assigned to
   */
  assignAnimalToEnclosure(animal: Animal, enclosure: Enclosure): Observable<any> {
    return this.httpClient.post(this.enclosureBaseUri + '/animal/' + enclosure.id, animal);
  }
}
