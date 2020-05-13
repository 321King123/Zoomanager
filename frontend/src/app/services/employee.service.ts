import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {Employee} from '../dtos/employee';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Message} from '../dtos/message';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private employeeBaseUri: string = this.globals.backendUri + '/employee';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  // /**
  //  * Loads all Employees from the backend
  //  */
  // getEmployees(): Observable<Employee[]> {
  //   return this.httpClient.get<Employee[]>(this.employeeBaseUri);
  // }
  //
  // /**
  //  * Loads specific employee from the backend
  //  * @param username of employee to load
  //  */
  // getMessageById(username: string): Observable<Employee> {
  //   console.log('Load employee details for ' + username);
  //   return this.httpClient.get<Employee>(this.employeeBaseUri + '/' + username);
  // }

  /**
   * Persists Employee to the backend
   * @param employee to persist
   */
  createEmployee(employee: Employee): Observable<Employee> {
    console.log('Create employee with username ' + employee.username);
    return this.httpClient.post<Employee>(this.employeeBaseUri, employee);
  }
}
