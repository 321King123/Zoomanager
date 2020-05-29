import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AnimalTask} from '../../dtos/animalTask';
import {TaskService} from '../../services/task.service';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {Employee} from '../../dtos/employee';
import {Animal} from '../../dtos/animal';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureService} from '../../services/enclosure.service';
import {EnclosureTask} from '../../dtos/enclosureTask';
import {AlertService} from '../../services/alert.service';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;


@Component({
  selector: 'app-task-creation',
  templateUrl: './task-creation.component.html',
  styleUrls: ['./task-creation.component.css']
})
export class TaskCreationComponent implements OnInit {
  task: AnimalTask;
  enclosureTask: EnclosureTask;

  componentId = 'task-creation';

  allEmployees: Employee[];
  allAnimals: Animal[];
  taskCreationForm: FormGroup;

  @Input() currentEmployee;
  @Input() animalsOfEmployee;
  @Input() enclosuresOfEmployee;
  employeesOfTaskSubject: Employee[];
  doctors: Employee[];
  janitors: Employee[];
  employeesFound = false;

  isEnclosureTask = false;
  isAnimalTask = true;

  @Output() reloadTasks = new EventEmitter();

  constructor(private taskService: TaskService, private animalService: AnimalService,
              private employeeService: EmployeeService, private formBuilder: FormBuilder,
              private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.getDoctors();
    this.getJanitors();
    this.taskCreationForm = this.formBuilder.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      assignedEmployeeUsername: [],
      subjectId: ['', Validators.required],
      priority: [false]
    });
  }

  getAllAnimals() {
    this.animalService.getAnimals().subscribe(
      (animals) => {
        this.allAnimals = animals;
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getAllAnimals');
      }
    );
  }

  getDoctors() {
    this.employeeService.getDoctors().subscribe(
      (doctors) => {
        this.doctors = doctors;
        DEBUG_LOG('Getting Doctors: ' + JSON.stringify(doctors));
      },
      error => {
                this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getDoctors');
      }
    );
  }

  getJanitors() {
    this.employeeService.getJanitors().subscribe(
      (janitors) => {
        this.janitors = janitors;
        DEBUG_LOG('Getting Janitors: ' + JSON.stringify(janitors));
      },
      error => {
                this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getJanitors');
      }
    );
  }

  getEmployeesOfAnimal() {
    this.employeesFound = false;
    this.employeeService.getEmployeesOfAnimal(this.taskCreationForm.controls.subjectId.value).subscribe(
      (employees) => {
        this.employeesOfTaskSubject = employees;
        this.employeesFound = true;
        DEBUG_LOG('Getting Employees of animal: ' + this.taskCreationForm.controls.subjectId.value);
      },
      error => {
                this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getEmployeesOfAnimal');
      }
    );
  }

  getEmployeesOfEnclosure() {
    this.employeesFound = false;
    this.employeeService.getEmployeesOfEnclosure(this.taskCreationForm.controls.subjectId.value).subscribe(
      (employees) => {
        this.employeesOfTaskSubject = employees;
        this.employeesFound = true;
      },
      error => {
                this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getEmployeesOfEnclosure');
      }
    );
  }

  taskSubmitted() {
    if (this.taskCreationForm.valid) {
      if (this.isAnimalTask) {
        this.getAnimalTaskFromForm();
        this.createAnimalTask();
      } else if (this.isEnclosureTask) {
        this.getEnclosureTaskFromForm();
        this.createEnclosureTask();
      }
    }
  }

  priorityTaskSubmitted() {
    this.taskCreationForm.controls['priority'].setValue(true);
    if (this.taskCreationForm.valid) {
      if (this.isAnimalTask) {
        this.getAnimalTaskFromForm();
        this.createAnimalTask();
      } else if (this.isEnclosureTask) {
        this.getEnclosureTaskFromForm();
        this.createEnclosureTask();
      }
    }
  }

  getAnimalTaskFromForm() {
    const startTimeParsed = this.parseDate(this.taskCreationForm.controls.startTime.value);
    const endTimeParsed = this.parseDate(this.taskCreationForm.controls.endTime.value);
    this.task = new AnimalTask(
      null,
      this.taskCreationForm.controls.title.value,
      this.taskCreationForm.controls.description.value,
      startTimeParsed,
      endTimeParsed,
      this.taskCreationForm.controls.assignedEmployeeUsername.value,
      null,
      this.taskCreationForm.controls.subjectId.value,
      null,
      this.taskCreationForm.controls.priority.value
    );
    if (this.task.assignedEmployeeUsername != null) {
      this.task.status = 'ASSIGNED';
    } else {
      this.task.status = 'NOT_ASSIGNED';
    }
  }

  getEnclosureTaskFromForm() {
    const startTimeParsed = this.parseDate(this.taskCreationForm.controls.startTime.value);
    const endTimeParsed = this.parseDate(this.taskCreationForm.controls.endTime.value);
    this.enclosureTask = new EnclosureTask(
      null,
      this.taskCreationForm.controls.title.value,
      this.taskCreationForm.controls.description.value,
      startTimeParsed,
      endTimeParsed,
      this.taskCreationForm.controls.assignedEmployeeUsername.value,
      null,
      this.taskCreationForm.controls.subjectId.value,
      null,
      this.taskCreationForm.controls.priority.value
    );
    if (this.enclosureTask.assignedEmployeeUsername != null) {
      this.enclosureTask.status = 'ASSIGNED';
    } else {
      this.enclosureTask.status = 'NOT_ASSIGNED';
    }
  }

  parseDate(dateUnparsed) {
    const parsed = new Date(dateUnparsed);
    const ten = function (x) {
      return x < 10 ? '0' + x : x;
    };
    const year = parsed.getFullYear();
    const month = parsed.getMonth() + 1;
    const day = parsed.getDate();
    const monthZero = (month < 10) ? '0' : '';
    const dayZero = (day < 10) ? '0' : '';
    const date = year + '-' + monthZero + month + '-' + dayZero + day;
    const time = ten(parsed.getHours()) + ':' + ten(parsed.getMinutes()) + ':' + ten(parsed.getSeconds());

    return date + ' ' + time;
  }

  clearForm() {
    this.taskCreationForm.reset();
  }

  onClose() {
    this.alertService.clear(this.componentId);
  }

  createEnclosureTask() {
    this.taskService.createNewTaskEnclosure(this.enclosureTask).subscribe(
      (res: any) => {
        this.clearForm();
        this.reloadTasks.emit();
        this.alertService.success('Task was successfully create!',
          {componentId: this.componentId, title: 'Success!'},
          'task-creation createEnclosureTask');
      },
      error => {
                this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation createEnclosureTask');

      }
    );
  }

  createAnimalTask() {
    this.taskService.createNewTask(this.task).subscribe(
      (res: any) => {
        this.clearForm();
        this.reloadTasks.emit();
        this.alertService.success('Task was successfully create!',
          {componentId: this.componentId, title: 'Success!'},
          'task-creation createEnclosureTask');
      },
      error => {
                this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation createAnimalTask');
      }
    );
  }

  setToAnimalTask() {
    if (this.isAnimalTask) {
      // Do nothing
    } else {
      this.isAnimalTask = true;
      this.isEnclosureTask = false;
      this.clearSubject();
    }
  }

  setToEnclosureTask() {
    if (this.isEnclosureTask) {
      // Do nothing
    } else {
      this.isAnimalTask = false;
      this.isEnclosureTask = true;
      this.clearSubject();
    }
  }

  clearSubject() {
    this.taskCreationForm.controls.subjectId.reset('', Validators.required);
    if (this.employeesOfTaskSubject !== undefined) {
      this.employeesOfTaskSubject.length = 0;
    }
  }

  clearAlerts() {
    this.alertService.clear(this.componentId);
  }

}
