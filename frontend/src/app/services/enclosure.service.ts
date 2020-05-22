import { Injectable } from '@angular/core';
import {Enclosure} from '../dtos/enclosure';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class EnclosureService {

  private enclosureBaseUri: string = this.globals.backendUri + '/enclosure';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  createEnclosure(enclosure: Enclosure): Observable<Enclosure> {
    console.log('Create Enclosure: ' + JSON.stringify(enclosure));
    return this.httpClient.post<Enclosure>(this.enclosureBaseUri, enclosure);
  }

  getAllEnclosures(): Observable<Enclosure[]> {
    console.log('Load all Enclosures.');
    return this.httpClient.get<Enclosure[]>(this.enclosureBaseUri);
  }
}
