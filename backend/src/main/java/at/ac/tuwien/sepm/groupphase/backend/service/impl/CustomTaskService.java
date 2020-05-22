package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.IncorrectTypeException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorisedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFreeException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class CustomTaskService implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TaskRepository taskRepository;

    private final EmployeeService employeeService;

    private final AnimalTaskRepository animalTaskRepository;

    private final EnclosureTaskRepository enclosureTaskRepository;

    @Autowired
    public CustomTaskService(TaskRepository taskRepository, AnimalTaskRepository animalTaskRepository,
                             EmployeeService employeeService, EnclosureTaskRepository enclosureTaskRepository) {
        this.taskRepository = taskRepository;
        this.animalTaskRepository = animalTaskRepository;
        this.employeeService = employeeService;
        this.enclosureTaskRepository = enclosureTaskRepository;
    }

    @Override
    public AnimalTask createAnimalTask(Task task, Animal animal) {
        LOGGER.debug("Creating new Animal Task");
        Employee employee = task.getAssignedEmployee();

        if(animal == null)
            throw new NotFoundException("Could not find animal with given Id");

        validateStartAndEndTime(task);

        if(employee == null) {
            task.setStatus(TaskStatus.NOT_ASSIGNED);
        }else if(employee.getType() == EmployeeType.JANITOR){
            throw new IncorrectTypeException("A Janitor cant complete an animal Task");
        }else{
            if(employee.getType() == EmployeeType.ANIMAL_CARE && !employeeService.isAssignedToAnimal(employee.getUsername(), animal.getId())){
                throw new NotAuthorisedException("You cant assign an animal caretaker that is not assigned to the animal.");
            }
            task.setStatus(TaskStatus.ASSIGNED);
        }


        if(task.getStatus() == TaskStatus.ASSIGNED && !employeeService.employeeIsFreeBetweenStartingAndEndtime(employee, task)){
            throw new NotFreeException("Employee already works on a task in the given time");
        }

        Task createdTask = taskRepository.save(task);
        AnimalTask animalTask = animalTaskRepository.save(AnimalTask.builder().id(createdTask.getId()).subject(animal).build());
        animalTask.setTask(createdTask);
        animalTask.setSubject(animal);
        return animalTask;
    }

    @Override
    public void deleteAnimalTasksBelongingToAnimal(Long animalId) {
        LOGGER.debug("Deleting Animal Task of Animal with Id " + animalId);
        List<AnimalTask> assignedAnimalTasks = animalTaskRepository.findAllBySubject_Id(animalId);
        animalTaskRepository.deleteAll(assignedAnimalTasks);
    }

    @Override
    public EnclosureTask createEnclosureTask(Task task, Enclosure enclosure) {
        LOGGER.debug("Creating new Animal Task");
        Employee employee = task.getAssignedEmployee();

        if(enclosure == null)
            throw new NotFoundException("Could not find enclosure with given Id");

        validateStartAndEndTime(task);

        //TO-DO: Type Validation
        task.setStatus(TaskStatus.NOT_ASSIGNED);

        Task createdTask = taskRepository.save(task);

        EnclosureTask enclosureTask = enclosureTaskRepository.save(EnclosureTask.builder().id(createdTask.getId()).subject(enclosure).build());
        enclosureTask.setTask(createdTask);
        enclosureTask.setSubject(enclosure);
        return enclosureTask;
    }

    private void validateStartAndEndTime(Task task) throws ValidationException {
        if(task.getStartTime().isAfter(task.getEndTime()))
            throw new ValidationException("Starting time of task cant be later than end time");

    }
}
