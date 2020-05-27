import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {AnimalTask} from '../dtos/animalTask';
import {Observable} from 'rxjs';
import {EnclosureTask} from '../dtos/enclosureTask';
import {Employee} from '../dtos/employee';
import {Task} from '../dtos/task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private animalTaskBaseUri: string = this.globals.backendUri + '/tasks/animal';
  private enclosureTaskBaseUri: string = this.globals.backendUri + '/tasks/enclosure';
  private taskBaseUri: string = this.globals.backendUri + '/tasks';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createNewTask(task: AnimalTask): Observable<AnimalTask> {
    console.log('Creating Task: ' + JSON.stringify(task));
    return this.httpClient.post<AnimalTask>(this.animalTaskBaseUri + '/' + task.animalId, task);
  }


  createNewTaskEnclosure(task: EnclosureTask): Observable<EnclosureTask> {
    console.log('Creating Task: ' + JSON.stringify(task));
    return this.httpClient.post<EnclosureTask>(this.enclosureTaskBaseUri + '/' + task.enclosureId, task);
  }

  assignTask(id, employee) {
    console.log('Assign Task: ' + id + ' to ' + JSON.stringify(employee));
    return this.httpClient.put<Employee>(this.taskBaseUri + '/' + id, employee);
  }

  getTasksOfAnimal(animalId): Observable<AnimalTask[]> {
    console.log('Get tasks of animal ' + animalId);
    return this.httpClient.get<AnimalTask[]>(this.animalTaskBaseUri + '/' + animalId);
  }

  deleteTask(id): Observable<any> {
    console.log('Delete Task: ' + id);
    return this.httpClient.delete(this.taskBaseUri + '/' + id);
  }

  getAnimalTasksOfEmployee(username): Observable<AnimalTask[]> {
    console.log('Get animal tasks of employee ' + username);
    return this.httpClient.get<AnimalTask[]>(this.taskBaseUri + '/employee/animal-task/' + username);
  }


  markTaskAsDone(taskId): Observable<any> {
    console.log('Mark task as done ' + taskId);
    return this.httpClient.put(this.taskBaseUri + '/finished/' + taskId, {});
  }

  getEnclosureTasksOfEmployee(username): Observable<EnclosureTask[]> {
    console.log('Get tasks of employee ' + username);
    return this.httpClient.get<EnclosureTask[]>(this.taskBaseUri + '/employee/enclosure-task/' + username);
  }

  getTasksOfEnclosure(enclosureId): Observable<EnclosureTask[]> {
    console.log('Get tasks of enclosure ' + enclosureId);
    return this.httpClient.get<EnclosureTask[]>(this.taskBaseUri + '/enclosure/' + enclosureId);

  }

  getTasksOfEmployee(username): Observable<Task[]> {
    console.log('Get all tasks of employee ' + username);
    return this.httpClient.get<Task[]>(this.taskBaseUri + '/employee/' + username);
  }
}
