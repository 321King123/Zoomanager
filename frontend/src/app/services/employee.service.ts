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

  /**
   * Get List of all current employees
   */
  getAllEmployees(): Observable<Employee[]> {
    console.log('Getting all employees');
    return this.httpClient.get<Employee[]>(this.employeeBaseUri);
  }

  /**
   * Get filtered List of current employees
   * @param employee contains search parameters (right now only name in form of substring and type relevant)
   */
  searchEmployees(employee: Employee): Observable<Employee[]> {
    console.log('Getting filtered list of employees type: ' + employee.type + ' name: ' + employee.name);
    let query = '/search?';
    if (employee.name != null && employee.name !== '') {
      query = query + 'name=' + employee.name + '&';
    }
    if (employee.type != null) {
      query = query + 'type=' + employee.type + '&';
    }
    query = query.substring(0, query.length - 1);
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + query);
  }
}
