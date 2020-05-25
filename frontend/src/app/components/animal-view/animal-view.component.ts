import {Component, OnInit} from '@angular/core';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {TaskService} from '../../services/task.service';
import {Animal} from '../../dtos/animal';
import {ActivatedRoute, Router} from '@angular/router';
import {Employee} from '../../dtos/employee';
import {AnimalTask} from '../../dtos/animalTask';
import {AuthService} from '../../services/auth.service';
import {EnclosureService} from '../../services/enclosure.service';
import {Enclosure} from '../../dtos/enclosure';

@Component({
  selector: 'app-animal-view',
  templateUrl: './animal-view.component.html',
  styleUrls: ['./animal-view.component.css']
})
export class AnimalViewComponent implements OnInit {
  error = false;
  errorMessage = '';
  currentAnimal: Animal;

  doctors: Employee[];
  employees: Employee[];
  tasks: AnimalTask[];
  selectedEnclosure: Enclosure;
  enclosureList: Enclosure[];
  selectedEmployee: Employee;
  employeeList: Employee[];

  constructor(private animalService: AnimalService, private employeeService: EmployeeService,
              private taskService: TaskService, private route: ActivatedRoute, private authService: AuthService,
              private enclosureService: EnclosureService) {
  }

  ngOnInit(): void {
    const currentAnimalId = (this.route.snapshot.paramMap.get('animalId'));
    this.getCurrentAnimal(currentAnimalId);
    this.getAllEnclosures();
    this.getAllEmployees();
  }

  getCurrentAnimal(id) {
    this.animalService.getAnimalById(id).subscribe(
      (a: Animal) => {
        this.currentAnimal = a;
        this.getEmployeesOfAnimal();
        this.getTasksOfAnimal();
        this.getDoctors();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getTasksOfAnimal() {
    this.taskService.getTasksOfAnimal(this.currentAnimal.id).subscribe(
      (tasks: AnimalTask[]) => {
        this.tasks = tasks;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
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

  vanishError() {
    this.error = false;
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

  getEmployeesOfAnimal() {
    this.employeeService.getEmployeesOfAnimal(this.currentAnimal.id).subscribe(
      (employees) => {
        this.employees = employees;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  deleteTask(animalTask: AnimalTask) {
    this.taskService.deleteTask(animalTask.id).subscribe(
      () => {
        this.getTasksOfAnimal();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  assignAnimaltoEnclosureOrEmployee() {
    if (this.selectedEnclosure != null) {

      this.enclosureService.assignAnimalToEnclosure(this.currentAnimal, this.selectedEnclosure).subscribe(
        () => {
          this.selectedEnclosure = null;
        },
        error => {
          console.log('Failed to assign enclosure');
          this.defaultServiceErrorHandling(error);
        }
      );
    }
    if (this.selectedEmployee != null) {
      this.employeeService.assignAnimalToEmployee(this.currentAnimal, this.selectedEmployee).subscribe(
        () => {
          this.selectedEmployee = null;
        },
        error => {
          console.log('Failed to assign employee');
          this.defaultServiceErrorHandling(error);
        }
      );
    }
  }

  private getAllEnclosures() {
    this.enclosureService.getAllEnclosures().subscribe(
      enclosures => {
        this.enclosureList = enclosures;
      },
      error => {
        if (error.status === 404) {
          this.enclosureList.length = 0;
        }
        console.log('Failed to load all enclosures');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private getAllEmployees() {
    this.employeeService.getAllEmployees().subscribe(
      employees => {
        this.employeeList = employees;
      },
      error => {
        if (error.status === 404) {
          this.employeeList.length = 0;
        }
        console.log('Failed to load all employees');
        this.defaultServiceErrorHandling(error);
      }
    );
  }
}
