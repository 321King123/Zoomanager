import { Injectable } from '@angular/core';
import {Enclosure} from '../dtos/enclosure';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Globals, Utilities} from '../global/globals';
import {Employee} from '../dtos/employee';
import {Animal} from '../dtos/animal';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Injectable({
  providedIn: 'root'
})
export class EnclosureService {

  private enclosureBaseUri: string = this.globals.backendUri + '/enclosure';
  private animalBaeseuri: string = this.globals.backendUri + '/animals';

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

  /**
   * Get enclosure where animal is already assigned
   * @param animal to check if already assigned
   */
  getAlreadyAssignedEnclosureToAnimal(selectedAnimal: Animal): Observable<Enclosure>  {
    return this.httpClient.get<Enclosure>(this.enclosureBaseUri + /animal/ + selectedAnimal.id) ;
  }

  /**
   * Delete enclosure that has no animal
   * @param enclosureToView enclosure to delete
   */
  deleteEnclosure(enclosureToView: Enclosure): Observable<any> {
    DEBUG_LOG('Delete enclosure ' + enclosureToView.id);
    return this.httpClient.delete<Enclosure>(this.enclosureBaseUri + '/' + enclosureToView.id);
  }

  unassignAnimal(selectedAnimal: Animal): Observable<any> {
    DEBUG_LOG('Remove animal from enclosure ' + JSON.stringify(selectedAnimal));
    return this.httpClient.put<Animal>(this.animalBaeseuri + '/removeEnclosure', selectedAnimal);
  }

  getEnclosuresOfEmployee(employeeUsername: string): Observable<Enclosure[]> {
    DEBUG_LOG('Get Enclosures by employee username: ' + employeeUsername);
    return this.httpClient.get<Enclosure[]>(this.enclosureBaseUri + /employee/ + employeeUsername);
  }
}
