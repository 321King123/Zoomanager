import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {AnimalTask} from '../dtos/animalTask';
import {Observable} from 'rxjs';
import {EnclosureTask} from '../dtos/enclosureTask';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private animalTaskBaseUri: string = this.globals.backendUri + '/tasks/animal';
  private enclosureTaskBaseUri: string = this.globals.backendUri + '/tasks/enclosure';

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
}
