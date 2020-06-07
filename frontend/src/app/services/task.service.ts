import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals, Utilities} from '../global/globals';
import {AnimalTask} from '../dtos/animalTask';
import {Observable} from 'rxjs';
import {EnclosureTask} from '../dtos/enclosureTask';
import {Employee} from '../dtos/employee';
import {Task} from '../dtos/task';
import DEBUG_LOG = Utilities.DEBUG_LOG;

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
    DEBUG_LOG('Creating Task: ' + JSON.stringify(task));
    return this.httpClient.post<AnimalTask>(this.animalTaskBaseUri + '/' + task.animalId, task);
  }

  createNewTaskEnclosure(task: EnclosureTask): Observable<EnclosureTask> {
    DEBUG_LOG('Creating Task: ' + JSON.stringify(task));
    return this.httpClient.post<EnclosureTask>(this.enclosureTaskBaseUri + '/' + task.enclosureId, task);
  }

  assignTask(id, employee) {
    DEBUG_LOG('Assign Task: ' + id + ' to ' + JSON.stringify(employee));
    return this.httpClient.put<Employee>(this.taskBaseUri + '/' + id, employee);
  }

  getTasksOfAnimal(animalId): Observable<Task[]> {
    DEBUG_LOG('Get tasks of animal ' + animalId);
    return this.httpClient.get<Task[]>(this.animalTaskBaseUri + '/' + animalId);
  }

  deleteTask(id): Observable<any> {
    DEBUG_LOG('Delete Task: ' + id);
    return this.httpClient.delete(this.taskBaseUri + '/' + id);
  }

  getAnimalTasksOfEmployee(username): Observable<AnimalTask[]> {
    DEBUG_LOG('Get animal tasks of employee ' + username);
    return this.httpClient.get<AnimalTask[]>(this.taskBaseUri + '/employee/animal-task/' + username);
  }

  markTaskAsDone(taskId): Observable<any> {
    DEBUG_LOG('Mark task as done ' + taskId);
    return this.httpClient.put(this.taskBaseUri + '/finished/' + taskId, {});
  }

  getEnclosureTasksOfEmployee(username): Observable<EnclosureTask[]> {
    DEBUG_LOG('Get enclosure tasks of employee ' + username);
    return this.httpClient.get<EnclosureTask[]>(this.taskBaseUri + '/employee/enclosure-task/' + username);
  }

  getTasksOfEnclosure(enclosureId): Observable<Task[]> {
    DEBUG_LOG('Get tasks of enclosure ' + enclosureId);
    return this.httpClient.get<Task[]>(this.taskBaseUri + '/enclosure/' + enclosureId);

  }

  getTasksOfEmployee(username): Observable<Task[]> {
    DEBUG_LOG('Get all tasks of employee ' + username);
    return this.httpClient.get<Task[]>(this.taskBaseUri + '/employee/' + username);
  }

  updateFullTaskInformation(task: Task): Observable<any> {
    DEBUG_LOG('Update full task information ' + JSON.stringify(task));
    return this.httpClient.put(this.taskBaseUri + '/update', task);
  }
}
