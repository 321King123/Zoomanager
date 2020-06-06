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

  highPriority = false;
  normalPriority = true;

  selectEmployeeTypeMode = false;
  employeeTypeSelected = false;
  employeeTypeForAutoAssignment;
  autoAssignSubmission = false;

  @Output() reloadTasks = new EventEmitter();
  submittedTask = false;

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
      priority: [false],
      duration: ['']
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

  taskWithAutoAssignSubmitted() {
    DEBUG_LOG('helloAuto0');
    this.autoAssignSubmission = true;
    this.taskSubmitted();
  }

  taskSubmitted() {
    DEBUG_LOG('hello0');
    this.submittedTask = true;
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
    DEBUG_LOG('hello0priority');
    this.submittedTask = true;
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
    DEBUG_LOG('hello1');
    let startTimeParsed;
    let endTimeParsed;
    if (this.highPriority) {
      startTimeParsed = this.parseDateForHighPriority(true);
      endTimeParsed = this.parseDateForHighPriority(false);
    } else {
      startTimeParsed = this.parseDate(this.taskCreationForm.controls.startTime.value);
      endTimeParsed = this.parseDate(this.taskCreationForm.controls.endTime.value);
    }

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
    DEBUG_LOG('hello1enclosure');
    let startTimeParsed;
    let endTimeParsed;
    if (this.highPriority) {
      startTimeParsed = this.parseDateForHighPriority(true);
      endTimeParsed = this.parseDateForHighPriority(false);
    } else {
      startTimeParsed = this.parseDate(this.taskCreationForm.controls.startTime.value);
      endTimeParsed = this.parseDate(this.taskCreationForm.controls.endTime.value);
    }

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

  parseDateForHighPriority(mode: boolean) {
    const ten = function (x) {
      return x < 10 ? '0' + x : x;
    };
    const date = new Date(Date.now());
    date.setFullYear(date.getFullYear() + 1);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const monthZero = (month < 10) ? '0' : '';
    const dayZero = (day < 10) ? '0' : '';

    const dateParsed = year + '-' + monthZero + month + '-' + dayZero + day;
    if (mode === true) {
      return dateParsed + ' 00:00:00';
    } else {
      const duration = this.taskCreationForm.controls.duration.value;
      const time = ten(duration.hour) + ':' + ten(duration.minute) + ':' + ten(duration.second);
      return dateParsed + ' ' + time;
    }

  }

  clearForm() {
    this.taskCreationForm.reset();
    this.submittedTask = false;
  }

  onClose() {
    this.alertService.clear(this.componentId);
  }

  createEnclosureTask() {
    this.taskService.createNewTaskEnclosure(this.enclosureTask).subscribe(
      (enclosureTask: EnclosureTask) => {
        this.clearForm();
        this.reloadTasks.emit();
        this.alertService.success('Task was successfully created!',
          {componentId: this.componentId, title: 'Success!'},
          'task-creation createEnclosureTask');
        if (this.autoAssignSubmission) {
          this.assignAfterCreation(enclosureTask.id, 'ENCLOSURE_TASK', this.employeeTypeForAutoAssignment);
        }
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
      (animalTask: AnimalTask) => {
        this.clearForm();
        this.reloadTasks.emit();
        this.alertService.success('Task was successfully created!',
          {componentId: this.componentId, title: 'Success!'},
          'task-creation createEnclosureTask');
        if (this.autoAssignSubmission) {
          this.assignAfterCreation(animalTask.id, 'ANIMAL_TASK', this.employeeTypeForAutoAssignment);
        }
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

  setToPriorityTask() {
    if (this.highPriority) {

    } else {
      this.taskCreationForm.controls.startTime.clearValidators();
      this.taskCreationForm.controls.endTime.clearValidators();
      this.taskCreationForm.controls.duration.setValidators([Validators.required]);
      this.taskCreationForm.updateValueAndValidity();
      this.highPriority = true;
      this.normalPriority = false;
      this.clearStartEndTimes();
    }
  }

  setToNonPriorityTask() {
    if (this.normalPriority) {

    } else {
      this.taskCreationForm.controls.startTime.setValidators([Validators.required]);
      this.taskCreationForm.controls.endTime.setValidators([Validators.required]);
      this.taskCreationForm.controls.duration.clearValidators();
      this.taskCreationForm.updateValueAndValidity();
      this.highPriority = false;
      this.normalPriority = true;
    }
  }

  clearSubject() {
    this.taskCreationForm.controls.subjectId.reset('', Validators.required);
    if (this.employeesOfTaskSubject !== undefined) {
      this.employeesOfTaskSubject.length = 0;
    }
  }

  clearStartEndTimes() {
    this.taskCreationForm.controls.startTime.reset('', Validators.required);
    this.taskCreationForm.controls.endTime.reset('', Validators.required);
  }

  clearAlerts() {
    this.alertService.clear(this.componentId);
  }

  switchSelectEmployeeTypeMode() {
    this.selectEmployeeTypeMode = !this.selectEmployeeTypeMode;
    this.employeeTypeSelected = false;
    this.employeeTypeForAutoAssignment = null;
  }

  autoAssignAnimalTaskToDoctor(taskId) {
    this.taskService.autoAssignAnimalTaskToDoctor(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToDoctor()');
        this.switchSelectEmployeeTypeMode();
      },
      error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToDoctor()');
      }
    );
  }

  autoAssignAnimalTaskToCaretaker(taskId) {
    this.taskService.autoAssignAnimalTaskToCaretaker(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToCaretaker()');
      }, error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToCaretaker()');
      }
    );
  }

  autoAssignEnclosureTaskToCaretaker(taskId) {
    this.taskService.autoAssignEnclosureTaskToCaretaker(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskTaskToCaretaker()');

      }, error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskTaskToCaretaker()');
      }
    );
  }

  autoAssignEnclosureTaskToJanitor(taskId) {
    this.taskService.autoAssignEnclosureTaskToJanitor(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskTaskToJanitor()');
      }, error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskTaskToJanitor()');
      }
    );
  }

  assignAfterCreation(taskId, taskType, employeeType) {
    if (taskType === 'ANIMAL_TASK') {
      if (employeeType === 'DOCTOR') {
        this.autoAssignAnimalTaskToDoctor(taskId);
      } else if (employeeType === 'CARETAKER') {
        this.autoAssignAnimalTaskToCaretaker(taskId);
      }
    } else if (taskType === 'ENCLOSURE_TASK') {
      if (employeeType === 'JANITOR') {
        this.autoAssignEnclosureTaskToJanitor(taskId);
      } else if (employeeType === 'CARETAKER') {
        this.autoAssignEnclosureTaskToCaretaker(taskId);
      }
    }
    this.switchSelectEmployeeTypeMode();
  }

  selectDoctor() {
    this.employeeTypeForAutoAssignment = 'DOCTOR';
    this.employeeTypeSelected = true;
  }

  selectCaretaker() {
    this.employeeTypeForAutoAssignment = 'CARETAKER';
    this.employeeTypeSelected = true;
  }

  selectJanitor() {
    this.employeeTypeForAutoAssignment = 'JANITOR';
    this.employeeTypeSelected = true;
  }
}
