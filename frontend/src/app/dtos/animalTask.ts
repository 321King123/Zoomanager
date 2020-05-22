export class AnimalTask {
  constructor(public id: number,
              public title: string,
              public description: string,
              public startTime: string,
              public endTime: string,
              public assignedEmployeeUsername: string,
              public status: string,
              public animalId: string,
              public animalName: string) {
  }
}
