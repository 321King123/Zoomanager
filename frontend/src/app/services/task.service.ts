import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {AnimalTask} from '../dtos/animalTask';
import {Observable} from 'rxjs';
import {Employee} from '../dtos/employee';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private animalTaskBaseUri: string = this.globals.backendUri + '/tasks/animal';
  private taskBaseUri: string = this.globals.backendUri + '/tasks';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createNewTask(task: AnimalTask): Observable<AnimalTask> {
    console.log('Creating Task: ' + JSON.stringify(task));
    return this.httpClient.post<AnimalTask>(this.animalTaskBaseUri + '/' + task.animalId, task);
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
    console.log('Get tasks of employee ' + username);
    return this.httpClient.get<AnimalTask[]>(this.taskBaseUri + '/employee/' + username);
  }

  markTaskAsDone(taskId): Observable<any> {
    return this.httpClient.put(this.taskBaseUri + '/finished/' + taskId, {});
  }
}
