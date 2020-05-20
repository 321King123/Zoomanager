package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeServiceTest implements TestData {

    @Autowired
    EmployeeService employeeService;

    @MockBean
    EmployeeRepository employeeRepository;

    @MockBean
    AnimalRepository animalRepository;

    private Employee anmial_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .build();

    private Employee doctor = Employee.builder()
        .username(USERNAME_DOCTOR_EMPLOYEE)
        .name(NAME_DOCTOR_EMPLOYEE)
        .birthday(BIRTHDAY_DOCTOR_EMPLOYEE)
        .type(TYPE_DOCTOR_EMPLOYEE)
        .email(EMAIL_DOCTOR_EMPLOYEE)
        .build();

    private Employee janitor = Employee.builder()
        .username(USERNAME_JANITOR_EMPLOYEE)
        .name(NAME_JANITOR_EMPLOYEE)
        .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .type(TYPE_JANITOR_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .build();

    private Animal horse = Animal.builder()
        .id(ANIMAL_ID)
        .name(ANIMAL_NAME_HORSE)
        .description(ANIMAL_DESCRIPTION_FAST)
        .species(ANIMAL_SPECIES_ARABIAN)
        .publicInformation(ANIMAL_PUBLIC_INFORMATION_FAMOUS)
        .build();

    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() {
        Mockito.when(employeeRepository.findAll()).thenReturn(new LinkedList<>());
        List<Employee> employees = employeeService.getAll();
        assertEquals(0, employees.size());
    }

    @Test
    public void filledRepository_findByNameAndTypeNoMatching_thenThrowNotFoundException() {
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Employee> example = Example.of(Employee.builder().name("nonExistent").type(EmployeeType.JANITOR).build(), customExampleMatcher);
        Mockito.when(employeeRepository.findAll(example)).thenReturn(new LinkedList<>());
        assertThrows(NotFoundException.class, () -> employeeService.findByNameAndType(Employee.builder().name("nonExistent").type(EmployeeType.JANITOR).build()));
    }

    @Test
    public void filledRepository_whenFindAll_thenAllEntries() {
        List<Employee> employees = new LinkedList<>();
        employees.add(janitor);
        employees.add(doctor);
        employees.add(anmial_caretaker);
        Mockito.when(employeeRepository.findAll()).thenReturn(employees);
        List<Employee> returnedEmployees = employeeService.getAll();
        returnedEmployees.containsAll(employees);
        assertEquals(3, returnedEmployees.size());
    }

    @Test
    public void findEmployeeByUsername_returnsRightEmployee() throws Exception{
        Mockito.when(employeeRepository.findEmployeeByUsername(anmial_caretaker.getUsername())).thenReturn(anmial_caretaker);
        Employee employee = employeeService.findByUsername(anmial_caretaker.getUsername());
        assertEquals(anmial_caretaker.getUsername(),employee.getUsername());
    }

    @Test
    public void createExistingEmployee_throwsAlreadyExistsException() throws Exception{
        Mockito.when(employeeRepository.findEmployeeByUsername(anmial_caretaker.getUsername())).thenReturn(anmial_caretaker);
        assertThrows(AlreadyExistsException.class,()->{employeeService.createEmployee(anmial_caretaker);});
    }

    @Test
    public void assignAnimalToEmployee(){
        Mockito.when(employeeRepository.findEmployeeByUsername(anmial_caretaker.getUsername())).thenReturn(anmial_caretaker);
        Employee employee = employeeService.findByUsername(anmial_caretaker.getUsername());
        List<Animal> animalsWithEmployee = new LinkedList<>();
        animalsWithEmployee.add(horse);
        Mockito.when(employeeRepository.findEmployeeByUsername(anmial_caretaker.getUsername())).thenReturn(anmial_caretaker);
        employeeService.assignAnimal(employee.getUsername(), horse.getId());
        Mockito.when(animalRepository.findAllByCaretakers(employee)).thenReturn(animalsWithEmployee);
        List<Animal> returnedAnimals = employeeService.findAssignedAnimals(employee.getUsername());
        assertEquals(1, returnedAnimals.size());
    }

    @Test
    public void assignAlreadyAssignedAnimalToEmployee()throws Exception{
        Mockito.when(employeeRepository.findEmployeeByUsername(anmial_caretaker.getUsername())).thenReturn(anmial_caretaker);
        Employee employee = employeeService.findByUsername(anmial_caretaker.getUsername());
        List<Animal> animalsWithEmployee = new LinkedList<>();
        animalsWithEmployee.add(horse);
        Mockito.when(animalRepository.findAllByCaretakers(employee)).thenReturn(animalsWithEmployee);
        assertThrows(AlreadyExistsException.class,()->{employeeService.assignAnimal(employee.getUsername(), horse.getId());});
    }
}