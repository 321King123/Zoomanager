package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomEmployeeService implements EmployeeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EmployeeRepository employeeRepository;
    private final AnimalRepository animalRepository;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final AnimalTaskRepository animalTaskRepository;
    private final EnclosureTaskRepository enclosureTaskRepository;
    private final EnclosureRepository enclosureRepository;

    @Autowired
    public CustomEmployeeService(UserService userService, EmployeeRepository employeeRepository,
                                 AnimalRepository animalRepository, TaskRepository taskRepository,
                                 AnimalTaskRepository animalTaskRepository, EnclosureTaskRepository enclosureTaskRepository,
                                 EnclosureRepository enclosureRepository) {
        this.animalTaskRepository = animalTaskRepository;
        this.employeeRepository = employeeRepository;
        this.animalRepository = animalRepository;
        this.userService =userService;
        this.taskRepository = taskRepository;
        this.enclosureTaskRepository = enclosureTaskRepository;
        this.enclosureRepository = enclosureRepository;
    }

    @Override
    public Employee createEmployee(Employee employee) {
        LOGGER.debug("Creating new employee.");
        Employee exists = employeeRepository.findEmployeeByUsername(employee.getUsername());
        if(exists==null) return employeeRepository.save(employee);
        throw new AlreadyExistsException("Employee with this username already exists");
    }

    public List<Employee> getAll(){
        LOGGER.debug("Getting List of all employees.");
        return employeeRepository.findAll();
    }

    //This function will be the general search List function right now only Name and Type fill be filtered
    @Override
    public List<Employee> findByNameAndType(Employee employee){
        LOGGER.debug("Getting filtered List of employees.");
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Employee> example = Example.of(Employee.builder().name(employee.getName()).type(employee.getType()).build(), customExampleMatcher);
        List<Employee> employees = employeeRepository.findAll(example);
        if(employees.isEmpty())
            throw new NotFoundException("No employee fits the given criteria");
        return employees;
    }

    @Override
    public List<Animal> findAssignedAnimals(String employeeUsername){
        LOGGER.debug("Getting List of all animals assigned to " + employeeUsername);
        Employee employee = employeeRepository.findEmployeeByUsername(employeeUsername);

        List<Animal> animals = animalRepository.findAllByCaretakers(employee);
        if(animals.isEmpty())
            throw new NotFoundException("No Animals assigned to " +employeeUsername);
        return animals;
    }

    @Override
    public List<Enclosure> findAssignedEnclosures(String employeeUsername){
        LOGGER.debug("Getting List of all enclosures assigned to " + employeeUsername);
        Employee employee = employeeRepository.findEmployeeByUsername(employeeUsername);

        List<Animal> animals = animalRepository.findAllByCaretakers(employee);
        List  <Enclosure> enclosures = new LinkedList<>();

        if(animals.isEmpty())
            throw new NotFoundException("No Animals assigned to " + employeeUsername);
        for (Animal a:animals) {
            if(a.getEnclosure()!=null){
                enclosures.add(a.getEnclosure());
            }
        }
        return enclosures;
    }


    @Override
    public void assignAnimal(String employeeUsername, long animalId) {
        LOGGER.debug("Assigning  " + employeeUsername);
        Employee employee = employeeRepository.findEmployeeByUsername(employeeUsername);
        if(employee.getType() == EmployeeType.ANIMAL_CARE) {
            List<Animal> assignedAnimals = animalRepository.findAllByCaretakers(employee);
            for(Animal a: assignedAnimals){
                if(a.getId() == animalId)
                {
                    throw new AlreadyExistsException("Animal is already assigned to this Caretaker");
                }
            }
            animalRepository.assignAnimalToCaretaker(employeeUsername, animalId);
        } else {
            throw new IncorrectTypeException("Trying to assign Animal to Employee that is not ANIMAL_CARE.");
        }
    }

    @Override
    public Employee findByUsername(String username) {
        LOGGER.debug("Getting Specific employee: " + username);
        return employeeRepository.findEmployeeByUsername(username);
    }

    @Override
    public void deleteEmployeeByUsername(String username){
        LOGGER.debug("Deleting employee with username: " + username);
        Employee employee = findByUsername(username);
        if(employee == null)
            throw new NotFoundException("No employee to delete: " + username);

        List<Task> tasks=taskRepository.findAllByAssignedEmployee(employee);
        for (Task t:tasks){
            t.setStatus(TaskStatus.NOT_ASSIGNED);
            t.setAssignedEmployee(null);
            taskRepository.save(t);
        }
        employeeRepository.delete(employee);
        userService.deleteUser(username);
    }

    @Override
    public boolean employeeIsFreeBetweenStartingAndEndtime(Employee employee, Task task) {
        LOGGER.debug("Checking if " + employee.getUsername() + " is free");
        List<Task> tasks = taskRepository.findAllByAssignedEmployee(employee);
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        for(Task t:tasks){
            LocalDateTime existingStart = t.getStartTime();
            LocalDateTime existingEnd = t.getEndTime();
            if(existingStart.equals(start) && existingEnd.equals(end))
                throw new NotFreeException("Employee " + employee.getUsername() + " already has work ("
                    +  existingStart.toString() + " till " + existingEnd.toString() + ") during this tasks " +
                    "time frame ("  + start.toString() + " till " + end.toString() +  ").");
            if(existingStart.isBefore(start) && existingEnd.isAfter(end))
                throw new NotFreeException("Employee " + employee.getUsername() + " has work (" +  existingStart.toString()
                    + " till " + existingEnd.toString() + ") that overlaps this tasks " +
                    "time frame ("  + start.toString() + " till " + end.toString() + ").");
            if(existingStart.isAfter(start) && existingStart.isBefore(end))
                throw new NotFreeException("Employee " + employee.getUsername() + " already has work (" +  existingStart.toString()
                    + " till " + existingEnd.toString() + ") that starts during this tasks " +
                    "time frame (" + start.toString() + " till " + end.toString() + ").");
            if(existingEnd.isAfter(start) && existingEnd.isBefore(end))
                throw new NotFreeException("Employee " + employee.getUsername() + " has work (" +  existingStart.toString()
                    + " till " + existingEnd.toString() +") that ends during this tasks " +
                    "time frame (" + start.toString() + " till " + end.toString() +  ").");
        }
        return true;
    }

    @Override
    public boolean isAssignedToAnimal(String username, Long animalID) {
        LOGGER.debug("Checking if " + username + " is assigned to animal with id " + animalID);
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        for(Animal a: employee.getAssignedAnimals()){
            if(a.getId().equals(animalID))
                return true;
        }
        return false;
    }

    @Override
    public boolean isAssignedToEnclosure(String username, Long enclosureId) {
        LOGGER.debug("Checking if " + username + " is assigned to enclosure with id " + enclosureId);
        for(Enclosure e: findAssignedEnclosures(username)){
            if(e.getId().equals(enclosureId))
                return true;
        }
        return false;
    }

    @Override
    public List<Employee> getAllAssignedToAnimal(Animal animal) {
        LOGGER.debug("Getting all employees assigned to animal with id " + animal.getId());
        return employeeRepository.findByAssignedAnimalsContains(animal);
    }


    @Override
    public List<Employee> getAllAssignedToEnclosure(Enclosure enclosure) {
        LOGGER.debug("Getting all employees assigned to enclosure with id " + enclosure.getId());

        List<Employee> assignedEmployees = employeeRepository.getEmployeesByEnclosureID(enclosure.getId());

        return assignedEmployees;
    }


    @Override
    public List<Employee> getAllDocotrs() {
        LOGGER.debug("Getting all employees of Type Doctor");
        return employeeRepository.findAllByType(EmployeeType.DOCTOR);
    }

    @Override
    public List<Employee> getAllJanitors() {
        LOGGER.debug("Getting all employees of Type Janitor");
        return employeeRepository.findAllByType(EmployeeType.JANITOR);
    }

    @Override
    public boolean hasTaskAssignmentPermissions(String usernameEmployee, Long taskId) {
        LOGGER.debug("Checking task permissions for username {} and task with id {}", usernameEmployee, taskId );
        Optional<Employee> optionalEmployee = employeeRepository.findById(usernameEmployee);
        if(optionalEmployee.isEmpty())
            throw new NotFoundException("Username doesnt belong to an Employee");
        Employee employee = optionalEmployee.get();
        if(employee.getType() == EmployeeType.DOCTOR || employee.getType() == EmployeeType.JANITOR )
            return false;
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if(optionalTask.isEmpty())
            throw new NotFoundException("Could not find Task with given Id");
        Task task = optionalTask.get();
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(task.getId());
        if(animalTask.isPresent()){
            return isAssignedToAnimal(employee.getUsername(), animalTask.get().getSubject().getId());
        }

        //TODO: if it is an Enclosure Task you have to add the check if there is Permission for this (so get the
        // EnclosureTask and then check if there is an assignment relation between the Employee and the Enclosure Task)
        Optional<EnclosureTask> enclosureTask = enclosureTaskRepository.findById(task.getId());
        if(enclosureTask.isPresent()){
            return isAssignedToEnclosure(employee.getUsername(), enclosureTask.get().getSubject().getId());
        }
        return false;
    }

    @Override
    public boolean canBeAssignedToTask(Employee employee, Task task) {
        LOGGER.debug("Checking assignment permissions for username {} and task with id {}", employee.getUsername(), task.getId());
        if(!employeeIsFreeBetweenStartingAndEndtime(employee, task))
            return false;
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(task.getId());
        if(animalTask.isPresent()){
            if(employee.getType() == EmployeeType.DOCTOR)
                return true;
            if(employee.getType() == EmployeeType.ANIMAL_CARE)
                return isAssignedToAnimal(employee.getUsername(), animalTask.get().getSubject().getId());
            if(employee.getType() == EmployeeType.JANITOR)
                throw new IncorrectTypeException("Employees of type Janitor can not be assigned to Animal Tasks");
        }

        //TODO: if it is an Enclosure Task you have to add the check if there is Permission for this (so get the
        // EnclosureTask and then check if there is an assignment relation between the Employee and the Enclosure Task/return false if Doctor etc.)
        Optional<EnclosureTask> enclosureTask = enclosureTaskRepository.findById(task.getId());
        if(enclosureTask.isPresent()){
            if(employee.getType() == EmployeeType.JANITOR)
                return true;
            if(employee.getType() == EmployeeType.ANIMAL_CARE)
                return isAssignedToEnclosure(employee.getUsername(), enclosureTask.get().getSubject().getId());
            if(employee.getType() == EmployeeType.DOCTOR)
                throw new IncorrectTypeException("Employees of type Janitor can not be assigned to Enclosure Tasks");
        }

        return false;
    }
}
