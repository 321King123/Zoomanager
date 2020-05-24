import {Injectable} from '@angular/core';
import {Globals} from '../global/globals';
import {Employee} from '../dtos/employee';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Message} from '../dtos/message';
import {Animal} from '../dtos/animal';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private employeeBaseUri: string = this.globals.backendUri + '/employee';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

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

  getDoctors(): Observable<Employee[]> {
    console.log('Getting all doctors.');
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + '/doctors');
  }

  getJanitors(): Observable<Employee[]> {
    console.log('Getting all janitors.');
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + '/janitors');
  }

  getEmployeesOfAnimal(animalId): Observable<Employee[]> {
    console.log('Getting employees of animal ' + JSON.stringify(animalId));
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + '/assigned/animal/' + animalId);
  }

  getEmployeesOfEnclosure(enclosureId): Observable<Employee[]> {
    console.log('Getting employees of enclosure ' + JSON.stringify(enclosureId));
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + '/assigned/enclosure/' + enclosureId);
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

  /**
   * Get all assigned animals of the employee
   * @param employee whose assigned animals will be returned
   */
  getAnimals(employee: Employee): Observable<Animal[]> {
    return this.httpClient.get<Animal[]>(this.employeeBaseUri + '/animal/' + employee.username);
  }

  /**
   * Assigns an animal to an employee
   * @param animal to be assigned
   * @param employee the animal will be assigned to
   */
  assignAnimalToEmployee(animal: Animal, employee: Employee): Observable<any> {
    return this.httpClient.post(this.employeeBaseUri + '/animal/' + employee.username, animal);
  }

  getEmployeeByUsername(username: string): Observable<Employee> {
    return this.httpClient.get<Employee>(this.employeeBaseUri + '/' + username);
  }

  getPersonalInfo(): Observable<Employee> {
    return this.httpClient.get<Employee>(this.employeeBaseUri + '/info');
  }

  deleteEmployee(username: string): Observable<Employee> {
    return this.httpClient.delete<Employee>(this.employeeBaseUri + '/' + username);
  }
}
