export class Task {
  constructor(public id: number,
              public title: string,
              public description: string,
              public startTime: string,
              public endTime: string,
              public assignedEmployeeUsername: string,
              public status: string,
              public subjectId: string,
              public subjectName: string,
              public isAnimalTask: boolean,
              public priority: boolean = false) {
  }
}
