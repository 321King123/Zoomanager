import {EmployeeType} from '../global/globals';

export class Employee {
  constructor(
    public username: string,
    public email: string,
    public password: string,
    public birthday: Date,
    public employeeType: EmployeeType) {
  }
}
