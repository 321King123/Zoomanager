import {Component, Input, OnInit} from '@angular/core';
import {AnimalTask} from '../../dtos/animalTask';
import {TaskService} from '../../services/task.service';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {Employee} from '../../dtos/employee';
import {Animal} from '../../dtos/animal';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureService} from '../../services/enclosure.service';


@Component({
  selector: 'app-task-creation',
  templateUrl: './task-creation.component.html',
  styleUrls: ['./task-creation.component.css']
})
export class TaskCreationComponent implements OnInit {
  task: AnimalTask;

  error = false;
  errorMessage = '';

  success = false;

  allEmployees: Employee[];
  allAnimals: Animal[];
  taskCreationForm: FormGroup;

  submittedTask = false;
  @Input() currentEmployee;
  @Input() animalsOfEmployee;
  @Input() enclosuresOfEmployee;
  employeesOfTaskSubject: Employee[];
  doctors: Employee[];
  janitors: Employee[];
  employeesFound = false;

  isEnclosureTask = false;
  isAnimalTask = true;

  constructor(private taskService: TaskService, private animalService: AnimalService,
              private employeeService: EmployeeService, private formBuilder: FormBuilder) {
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
      subjectId: ['', Validators.required]
    });
  }

  getAllAnimals() {
    this.animalService.getAnimals().subscribe(
      (animals) => {
        this.allAnimals = animals;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getDoctors() {
    this.employeeService.getDoctors().subscribe(
      (doctors) => {
        this.doctors = doctors;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getJanitors() {
    this.employeeService.getJanitors().subscribe(
      (janitors) => {
        this.janitors = janitors;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getEmployeesOfAnimal() {
    this.employeesFound = false;
    this.employeeService.getEmployeesOfAnimal(this.taskCreationForm.controls.subjectId.value).subscribe(
      (employees) => {
        this.employeesOfTaskSubject = employees;
        this.employeesFound = true;
      },
      error => {
        this.defaultServiceErrorHandling(error);
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
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  vanishSuccess() {
    this.success = false;
  }

  taskSubmitted() {
    this.error = false;
    this.success = false;
    this.submittedTask = true;
    if (this.taskCreationForm.valid) {
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
        null
      );
      if (this.task.assignedEmployeeUsername != null) {
        this.task.status = 'ASSIGNED';
      } else {
        this.task.status = 'NOT_ASSIGNED';
      }
      this.createTask();
      this.clearForm();
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
    this.submittedTask = false;
  }

  createTask() {
    this.taskService.createNewTask(this.task).subscribe(
      (res: any) => {
        this.success = true;
      },
      error => {
        this.defaultServiceErrorHandling(error);
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

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

}
