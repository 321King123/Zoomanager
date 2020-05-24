package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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

        if(employee == null) {
            task.setStatus(TaskStatus.NOT_ASSIGNED);
        }else if(employee.getType() == EmployeeType.DOCTOR){
            throw new IncorrectTypeException("A Doctor cant complete an Enclosure Task");
        }else{
            if(employee.getType() == EmployeeType.ANIMAL_CARE
                && !employeeService.isAssignedToEnclosure(employee.getUsername(), enclosure.getId())){
                throw new NotAuthorisedException("You cant assign an animal caretaker that is not assigned to an animal in the Enclosure.");
            }
            task.setStatus(TaskStatus.ASSIGNED);
        }

        if(task.getStatus() == TaskStatus.ASSIGNED && !employeeService.employeeIsFreeBetweenStartingAndEndtime(employee, task)){
            throw new NotFreeException("Employee already works on a task in the given time");
        }

        task.setStatus(TaskStatus.ASSIGNED);

        Task createdTask = taskRepository.save(task);

        enclosureTaskRepository.save(EnclosureTask.builder().id(createdTask.getId()).subject(enclosure).build());
        EnclosureTask enclosureTask = enclosureTaskRepository.findEnclosureTaskById(createdTask.getId());
        return enclosureTask;
    }

    private void validateStartAndEndTime(Task task) throws ValidationException {
        if(task.getStartTime().isAfter(task.getEndTime()))
            throw new ValidationException("Starting time of task cant be later than end time");

    }

    @Override
    public void updateTask(Long taskId, Employee assignedEmployee) {
        LOGGER.debug("Assigning Task with id {} to employee with username {}", taskId, assignedEmployee.getUsername());
        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isEmpty())
            throw new NotFoundException("Could not find Task with given Id");
        Task foundTask = task.get();
        if(foundTask.getStatus() != TaskStatus.NOT_ASSIGNED){
            throw new IncorrectTypeException("Only currently unassigned Tasks can be assigned to an Employee");
        }
        Employee employee = employeeService.findByUsername(assignedEmployee.getUsername());
        if(employeeService.canBeAssignedToTask(employee, foundTask)){
            foundTask.setAssignedEmployee(employee);
            foundTask.setStatus(TaskStatus.ASSIGNED);
            taskRepository.save(foundTask);
        }else{
            throw new IncorrectTypeException("Employee does not fulfill assignment criteria");
        }
    }

    public List<AnimalTask> getAllTasksOfAnimal(Long animalId){
        LOGGER.debug("Get All Tasks belonging to Animal with id: {}", animalId);
        return animalTaskRepository.findAllBySubject_Id(animalId);
    }

    @Override
    public void deleteTask(Long taskId) {
        LOGGER.debug("Deleting Task with id {}", taskId);
        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isEmpty()) {
            throw new NotFoundException("Could not find Task with given Id");
        }
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(taskId);
        if(animalTask.isEmpty()) {
            throw new NotFoundException("Could not find Animal Task with given Id");
        } else {
            AnimalTask foundAnimalTask = animalTask.get();
            Task foundTask = task.get();
            animalTaskRepository.delete(foundAnimalTask);
            taskRepository.delete(foundTask);
        }

        //TODO: add handling of EnclosureTasks
        Optional<EnclosureTask> enclosureTask = enclosureTaskRepository.findById(taskId);
        if(enclosureTask.isEmpty()) {
            throw new NotFoundException("Could not find Enclosure Task with given Id");
        } else {
            enclosureTaskRepository.deleteEnclosureTaskAndBaseTaskById(taskId);
        }
    }

    @Override
    public List<AnimalTask> getAllAnimalTasksOfEmployee(String employeeUsername) {
        LOGGER.debug("Get All Tasks belonging to employee with username: {}", employeeUsername);
        Employee employee = employeeService.findByUsername(employeeUsername);
        if(employee == null)
            throw new NotFoundException("Could not find Employee with given Username");
        List<Task> taskList = new LinkedList<>(taskRepository.findAllByAssignedEmployee(employee));
        List<AnimalTask> animalTaskList = new LinkedList<>();
        for(Task t:taskList){
            Optional<AnimalTask> animalTask = animalTaskRepository.findById(t.getId());
            animalTask.ifPresent(animalTaskList::add);
        }
        return animalTaskList;
    }
}
