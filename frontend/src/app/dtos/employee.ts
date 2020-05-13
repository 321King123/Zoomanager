import {type} from '../global/globals';

export class Employee {
  constructor(
    public username: string,
    public email: string,
    public password: string,
    public name: string,
    public birthday: Date,
    public type: type) {
  }
}
