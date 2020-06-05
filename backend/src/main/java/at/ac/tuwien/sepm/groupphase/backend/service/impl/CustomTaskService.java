package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RepeatableTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    private final RepeatableTaskRepository repeatableTaskRepository;

    @Autowired
    public CustomTaskService(TaskRepository taskRepository, AnimalTaskRepository animalTaskRepository,
                             EmployeeService employeeService, EnclosureTaskRepository enclosureTaskRepository,
                             RepeatableTaskRepository repeatableTaskRepository) {
        this.taskRepository = taskRepository;
        this.animalTaskRepository = animalTaskRepository;
        this.employeeService = employeeService;
        this.enclosureTaskRepository = enclosureTaskRepository;
        this.repeatableTaskRepository = repeatableTaskRepository;
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
        List<AnimalTask> assignedAnimalTasks = animalTaskRepository.findAllAnimalTasksBySubject_Id(animalId);
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
            throw new NotFreeException("The employee does not work at the given time!");
        }

        Task createdTask = taskRepository.save(task);

        enclosureTaskRepository.save(EnclosureTask.builder().id(createdTask.getId()).subject(enclosure).build());
        return enclosureTaskRepository.findEnclosureTaskById(createdTask.getId());
    }

    private void validateStartAndEndTime(Task task) throws ValidationException {
        if(task.getStartTime().isAfter(task.getEndTime()))
            throw new ValidationException("Starting time of task cant be later than end time");

    }

    @Override
    public void updateTask(Long taskId, Employee assignedEmployee) {
        LOGGER.debug("Assigning Task with id {} to employee with username {}", taskId, assignedEmployee.getUsername());
        Task foundTask = getTaskById(taskId);
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
        return animalTaskRepository.findAllAnimalTasksBySubject_Id(animalId);
    }

    @Override
    public void deleteTask(Long taskId) {
        LOGGER.debug("Deleting Task with id {}", taskId);

        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isEmpty()) {
            throw new NotFoundException("Could not find Task with given Id");
        }
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(taskId);
        Optional<EnclosureTask> enclosureTask = enclosureTaskRepository.findById(taskId);
        Optional<RepeatableTask> repeatableTask = repeatableTaskRepository.findById(taskId);

        if(!animalTask.isEmpty() && !enclosureTask.isEmpty()) {
            throw new InvalidDatabaseStateException("Task is both Animal and Enclosure Task, this should not happen.");
        }
        repeatableTask.ifPresent(this::deleteRepeatableTask);
        if (!animalTask.isEmpty()){
            AnimalTask foundAnimalTask = animalTask.get();
            Task foundTask = task.get();
            animalTaskRepository.delete(foundAnimalTask);
            taskRepository.delete(foundTask);
        } else if (!enclosureTask.isEmpty()) {
            enclosureTaskRepository.deleteEnclosureTaskAndBaseTaskById(taskId);
        }
    }

    @Override
    public List<AnimalTask> getAllAnimalTasksOfEmployee(String employeeUsername) {
        LOGGER.debug("Get All Animal Tasks belonging to employee with username: {}", employeeUsername);
        Employee employee = employeeService.findByUsername(employeeUsername);
        if(employee == null)
            throw new NotFoundException("Could not find Employee with given Username");
        List<Task> taskList = new LinkedList<>(taskRepository.findAllByAssignedEmployeeOrderByStartTime(employee));
        List<AnimalTask> animalTaskList = new LinkedList<>();
        for(Task t:taskList){
            Optional<AnimalTask> animalTask = animalTaskRepository.findById(t.getId());
            animalTask.ifPresent(animalTaskList::add);
        }
        return animalTaskList;
    }

    @Override
    public void markTaskAsDone(Long taskId) {
        LOGGER.debug("Marking task with id {} as done", taskId);
        Task foundTask = getTaskById(taskId);
        foundTask.setStatus(TaskStatus.DONE);
        taskRepository.save(foundTask);
    }

    @Override
    public boolean isTaskPerformer(String employeeUsername, Long taskId) {
        LOGGER.debug("Check if employee with username {} is performing task with id {}", employeeUsername, taskId);
        Task task = getTaskById(taskId);
        return task.getAssignedEmployee().getUsername().equals(employeeUsername);
    }

    private Task getTaskById(Long taskId){
        LOGGER.debug("Find task with id {}", taskId);
        Optional<Task> task = taskRepository.findById(taskId);
        Task foundTask;
        if(task.isPresent()){
            foundTask = task.get();
        }else{
            throw new NotFoundException("Could not find Task with given Id");
        }
        return foundTask;
    }

    private AnimalTask getAnimalTaskById(Long animalTaskId){
        LOGGER.debug("Find animal task with id {}", animalTaskId);
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(animalTaskId);
        AnimalTask foundTask;
        if(animalTask.isPresent()){
            foundTask = animalTask.get();
        }else{
            throw new NotFoundException("Could not find Task with given Id");
        }
        return foundTask;
    }

    @Override
    public List<EnclosureTask> getAllEnclosureTasksOfEmployee(String employeeUsername) {
        LOGGER.debug("Get All Enclosure Tasks belonging to employee with username: {}", employeeUsername);
        validateEmployeeExists(employeeUsername);
        List<EnclosureTask> enclosureTasks = enclosureTaskRepository.findEnclosureTaskByEmployeeUsername(employeeUsername);
        return enclosureTasks;
    }

    @Override
    public List<EnclosureTask> getAllTasksOfEnclosure(Long enclosureId) {
        LOGGER.debug("Get All Tasks belonging to Enclosure with id: {}", enclosureId);
        return enclosureTaskRepository.findAllEnclosureTasksBySubject_Id(enclosureId);
    }

    private void validateEmployeeExists(String employeeUsername) {
        Employee employee = employeeService.findByUsername(employeeUsername);
        if(employee == null)
            throw new NotFoundException("Could not find Employee with given Username");
    }

    @Override
    public List<AnimalTask> createRepeatableAnimalTask(Task task, Animal animal, int amount, ChronoUnit separation, int separationCount) {
        validateStartAndEndTime(task);

        LocalDateTime newStartTime = task.getStartTime().plus(separationCount, separation);
        LocalDateTime newEndTime = task.getEndTime().plus(separationCount, separation);

        Task newTask = Task.builder().title(task.getTitle())
            .description(task.getDescription())
            .startTime(newStartTime)
            .endTime(newEndTime)
            .assignedEmployee(task.getAssignedEmployee())
            .status(task.getStatus())
            .priority(task.isPriority())
            .build();

        List<AnimalTask> animalTasks = new LinkedList<>();

        Task nextTask = null;

        if(amount > 1) {
            animalTasks = createRepeatableAnimalTask(newTask, animal, amount - 1, separation, separationCount);
            nextTask = animalTasks.get(animalTasks.size()-1).getTask();
        }

        AnimalTask thisTask = createAnimalTask(task, animal);

        repeatableTaskRepository.save(RepeatableTask.builder().id(thisTask.getId()).followTask(nextTask).build());

        animalTasks.add(thisTask);

        return animalTasks;
    }

    private void deleteRepeatableTask(RepeatableTask repeatableTask) {
        Optional<RepeatableTask> previousTask = repeatableTaskRepository.findByFollowTask(repeatableTask.getTask());
        if(previousTask.isPresent()) {
            RepeatableTask previousTask1 = previousTask.get();
            previousTask1.setFollowTask(repeatableTask.getFollowTask());
            repeatableTaskRepository.save(previousTask1);
        }
        repeatableTaskRepository.delete(repeatableTask);
    }
}
