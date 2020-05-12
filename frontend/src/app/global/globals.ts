import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Globals {
  readonly backendUri: string = 'http://localhost:8080/api/v1';
}
export enum EmployeeType {
  ANIMAL_CARE,
  DOCTOR,
  JANITOR
}
