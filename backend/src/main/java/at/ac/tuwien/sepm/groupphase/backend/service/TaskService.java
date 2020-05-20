package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;

public interface TaskService {

    /**
     * Used for creating new AnimalTasks
     * Also creates ne entry in Task Table
     * @param animalTask to be created
     * */
    AnimalTask createAnimalTask(AnimalTask animalTask);

}
