package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class CustomEmployeeService implements EmployeeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EmployeeRepository employeeRepository;

    @Autowired
    public CustomEmployeeService(UserService userService, EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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
        List<Employee> employees = employeeRepository.findAll();
        if(employees.isEmpty())
            throw new NotFoundException("There are currently no employees");
        return employees;
    }

    //This function will be the general search List function right now only Name and Type fill be filtered
    public List<Employee> findByNameAndType(Employee employee){
        LOGGER.debug("Getting filtered List of employees.");
        LOGGER.debug("Getting filtered List of employees.");
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Employee> example = Example.of(Employee.EmployeeBuilder.anEmployee().withName(employee.getName()).withType(employee.getType()).build(), customExampleMatcher);
        List<Employee> employees = employeeRepository.findAll(example);
        if(employees.isEmpty())
            throw new NotFoundException("No employee fits the given criteria");
        return employees;
    }
}
