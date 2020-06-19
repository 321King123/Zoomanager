import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Task} from '../../dtos/task';
import {Employee} from '../../dtos/employee';
import {Animal} from '../../dtos/animal';
import {Enclosure} from '../../dtos/enclosure';
import {EmployeeService} from '../../services/employee.service';
import {AnimalService} from '../../services/animal.service';
import {TaskService} from '../../services/task.service';
import {EnclosureService} from '../../services/enclosure.service';
import {AlertService} from '../../services/alert.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-task-info-update',
  templateUrl: './task-info-update.component.html',
  styleUrls: ['./task-info-update.component.css']
})
export class TaskInfoUpdateComponent implements OnInit {
  @Input() stringId: String = 'default';
  @Input() task: Task;
  copyTask: Task;
  @Input() index;
  @Input() animalsOfEmployee;
  @Input() enclosuresOfEmployee;
  @Output() reloadTasks = new EventEmitter();
  @Input() currentEmployee;

  @Input() editModeAlloved;

  assignee: Employee;
  animal: Animal;
  enclosure: Enclosure;

  componentId = 'TaskInfoUpdate';

  editMode: boolean;
  infoMode: boolean;
  repeatMode = false;

  animals: Animal[];
  enclosures: Enclosure[];

  doctors: Employee[];
  janitors: Employee[];
  employeesFound: Employee[];
  editTask: Task;
  validEditTask = true;
  taskValidationError = {
    title: false,
    description: false,
    startTime: false,
    endTime: false,
    subjectId: false,
    priority: false
  };

  @Output() toggleClickPropagationEvent = new EventEmitter();

  @ViewChild('modalToggleBtn')
  modalToggle: ElementRef<HTMLElement>;

  private modalIsOpen: boolean = false;

  constructor(private employeeService: EmployeeService, private animalService: AnimalService,
              private taskService: TaskService, private formBuilder: FormBuilder,
              private enclosureService: EnclosureService, private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.toInfoMode();
    this.getSubject();
    this.getDoctors();
    this.getJanitors();
  }

  getAssignee() {
    if (this.task.assignedEmployeeUsername !== null) {
      this.employeeService.getEmployeeByUsername(this.task.assignedEmployeeUsername).subscribe(
        employee => {
          this.assignee = employee;
        },
        error => {
          this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskInfoUpdate: getAssignee()');
        }
      );
    }
  }

  getSubject() {
    if (this.editModeAlloved && this.task.animalTask === true) {
      this.animalService.getAnimalById(this.task.subjectId).subscribe(
        animal => {
          this.animal = animal;
        },
        error => {
          this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskInfoUpdate: getSubject()');
        }
      );
    } else if (this.editModeAlloved && this.task.animalTask === false) {
      this.enclosureService.getById(this.task.subjectId).subscribe(
        enclosure => {
          this.enclosure = enclosure;
        },
        error => {
          this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskInfoUpdate: getSubject()');
        }
      );
    }
  }

  toInfoMode() {
    this.infoMode = true;
    this.editMode = false;
  }

  toEditMode() {
    this.editTask = this.task;
    if (this.task.animalTask) {
      DEBUG_LOG('Animal task info.');
      this.getEmployeesOfAnimal();
    } else {
      DEBUG_LOG('Enclosure task info.');
      this.getEmployeesOfEnclosure();
    }
    this.infoMode = false;
    this.editMode = true;
  }

  getDoctors() {
    this.employeeService.getDoctors().subscribe(
      (doctors) => {
        this.doctors = doctors;
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'TaskInfoUpdate: getDoctors');
      }
    );
  }

  getJanitors() {
    this.employeeService.getJanitors().subscribe(
      (janitors) => {
        this.janitors = janitors;
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'TaskInfoUpdate: getJanitors');
      }
    );
  }

  getEmployeesOfAnimal() {

    this.employeeService.getEmployeesOfAnimal(this.editTask.subjectId).subscribe(
      (employees) => {
        this.employeesFound = employees;
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'TaskInfoUpdate: getEmployeesOfAnimal');
      }
    );
  }

  getEmployeesOfEnclosure() {

    this.employeeService.getEmployeesOfEnclosure(this.editTask.subjectId).subscribe(
      (employees) => {
        this.employeesFound = employees;
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'TaskInfoUpdate: getEmployeesOfEnclosure');
      }
    );
  }

  clearValidationErrors() {
    this.validEditTask = true;
    this.taskValidationError = {
      title: false,
      description: false,
      startTime: false,
      endTime: false,
      subjectId: false,
      priority: false
    };
  }

  taskEditSubmitted() {
    this.clearValidationErrors();
    this.validateEditedTask();
    if (this.validEditTask) {
      if(this.repeatMode) {
        this.taskService.updateTaskInformationRepeat(this.editTask).subscribe(
          (res: any) => {
            this.task = this.editTask;
            this.toInfoMode();
          },
          error => {
            this.alertService.alertFromError(error,
              {componentId: this.componentId},
              'TaskInfoUpdate: taskEditSubmitted');
          }
        );
      } else {
        this.taskService.updateFullTaskInformation(this.editTask).subscribe(
          (res: any) => {
            this.task = this.editTask;
            this.toInfoMode();
          },
          error => {
            this.alertService.alertFromError(error,
              {componentId: this.componentId},
              'TaskInfoUpdate: taskEditSubmitted');
          }
        );
      }
    }
  }

  validateEditedTask() {
    if (this.editTask.title == null || this.editTask.title === '') {
      this.taskValidationError.title = true;
      this.validEditTask = false;
    }
    if (this.editTask.description == null || this.editTask.description === '') {
      this.taskValidationError.description = true;
      this.validEditTask = false;
    }
    if (this.editTask.startTime == null || this.editTask.startTime === '' && !this.repeatMode) {
      this.taskValidationError.startTime = true;
      this.validEditTask = false;
    }
    if (this.editTask.endTime == null || this.editTask.endTime === '' && !this.repeatMode) {
      this.taskValidationError.endTime = true;
      this.validEditTask = false;
    }
    if (this.editTask.subjectId == null || this.editTask.subjectId === '') {
      this.taskValidationError.subjectId = true;
      this.validEditTask = false;
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

  changeRepeat() {
    this.repeatMode = !this.repeatMode;
  }

  toggleModal() {
    //   this.stopClickPropagationEvent.emit();
    DEBUG_LOG('Toggle Modal');
    const modalToggleElement: HTMLElement = this.modalToggle.nativeElement;
    if (this.modalIsOpen) {
      this.closeModal(modalToggleElement);
    } else if (!this.modalIsOpen) {
      this.openModal(modalToggleElement);
    }

    // modalToggleElement.click();
    // this.enableClickPropagationEvent.emit();
  }

  closeModal(modalToggleElement: HTMLElement) {
    DEBUG_LOG('close modal, modalIsOpen = ' + this.modalIsOpen);
    if (this.modalIsOpen) {
      modalToggleElement.click();
      this.modalIsOpen = false;
      this.toggleClickPropagationEvent.emit();
    }

  }

  openModal(modalToggleElement: HTMLElement) {
    DEBUG_LOG('open modal, modalIsOpen = ' + this.modalIsOpen);
    if (!this.modalIsOpen) {
      this.toggleClickPropagationEvent.emit();
      this.modalIsOpen = true;
      modalToggleElement.click();
    }
  }
}
