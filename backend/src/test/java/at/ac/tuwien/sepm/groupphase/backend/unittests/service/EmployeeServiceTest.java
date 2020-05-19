package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeServiceTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    CustomUserDetailService userDetailService;

    @Autowired
    EmployeeService employeeService;

    @MockBean
    EmployeeRepository employeeRepository;

    private UserLogin animal_caretaker_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private Employee anmial_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .build();

    private UserLogin doctor_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_DOCTOR_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private Employee doctor = Employee.builder()
        .username(USERNAME_DOCTOR_EMPLOYEE)
        .name(NAME_DOCTOR_EMPLOYEE)
        .birthday(BIRTHDAY_DOCTOR_EMPLOYEE)
        .type(TYPE_DOCTOR_EMPLOYEE)
        .email(EMAIL_DOCTOR_EMPLOYEE)
        .build();

    private UserLogin janitor_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_JANITOR_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private Employee janitor = Employee.builder()
        .username(USERNAME_JANITOR_EMPLOYEE)
        .name(NAME_JANITOR_EMPLOYEE)
        .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .type(TYPE_JANITOR_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .build();

    @BeforeEach
    public void beforeEach() {
        //TODO: delete all data once we have the method for it
    }

    //TODO:uncomment once delete method is in beforeEach
    /*
    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() {
        List<Employee> employees = employeeService.getAll();
        assertEquals(0, employees.size());
    }
     */

    //currently fails if the users/employees already exist
    public void filledRepository_combinedSearch_resultContainsSubstringAndType() {

        userDetailService.createNewUser(animal_caretaker_login);
        userDetailService.createNewUser(doctor_login);
        userDetailService.createNewUser(janitor_login);
        employeeService.createEmployee(anmial_caretaker);
        employeeService.createEmployee(doctor);
        employeeService.createEmployee(janitor);
        List<Employee> employees = employeeService.findByNameAndType(Employee.builder().type(EmployeeType.JANITOR).name("aN").build());
        assertEquals(1, employees.size());
        assertEquals(employees.get(0).getUsername(), janitor.getUsername());
    }

    @Test
    public void findEmployeeByUsername_returnsRightEmployee() throws Exception{
        Mockito.when(employeeRepository.findEmployeeByUsername(animal_caretaker_login.getUsername())).thenReturn(anmial_caretaker);
        Employee employee = employeeService.findByUsername(anmial_caretaker.getUsername());
        assertEquals(anmial_caretaker.getUsername(),employee.getUsername());
    }

    @Test
    public void createExistingEmployee_throwsAlreadyExistsException() throws Exception{
        Mockito.when(employeeRepository.findEmployeeByUsername(anmial_caretaker.getUsername())).thenReturn(anmial_caretaker);
        assertThrows(AlreadyExistsException.class,()->{employeeService.createEmployee(anmial_caretaker);});
    }
}