package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.exception.IncorrectTypeException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomEmployeeService implements EmployeeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EmployeeRepository employeeRepository;
    private final AnimalRepository animalRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public CustomEmployeeService(UserService userService, EmployeeRepository employeeRepository, AnimalRepository animalRepository, TaskRepository taskRepository) {
        this.employeeRepository = employeeRepository;
        this.animalRepository = animalRepository;
        this.taskRepository = taskRepository;
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
    public boolean employeeIsFreeBetweenStartingAndEndtime(Employee employee, Task task){
        List<Task> tasks = taskRepository.findAllByAssignedEmployee(employee);
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        for(Task t:tasks){
            if(t.getStartTime().equals(start) && t.getEndTime().equals(end))
                return false;
            if(t.getStartTime().isBefore(start) && t.getEndTime().isAfter(end))
                return false;
            if(t.getStartTime().isAfter(start) && t.getStartTime().isBefore(end))
                return false;
            if(t.getEndTime().isAfter(start) && t.getEndTime().isBefore(end))
                return false;
        }
        return true;
    }

    @Override
    public boolean isAssignedToAnimal(String username, Long animalID) {
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        for(Animal a: employee.getAssignedAnimals()){
            if(a.getId().equals(animalID))
                return true;
        }
        return false;
    }

    @Override
    public List<Employee> getAllAssignedToAnimal(Animal animal) {
        return employeeRepository.findByAssignedAnimalsContains(animal);
    }
}
