package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeServiceTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    CustomUserDetailService userDetailService;

    @Autowired
    EmployeeService employeeService;

    private UserLogin animal_caretaker_login = UserLogin.UserBuilder.aUser()
        .withIsAdmin(false)
        .withUsername(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .withPassword(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private Employee anmial_caretaker = Employee.EmployeeBuilder.anEmployee()
        .withUsername(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .withName(NAME_ANIMAL_CARE_EMPLOYEE)
        .withBirthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .withType(TYPE_ANIMAL_CARE_EMPLOYEE)
        .withEmail(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .build();

    private UserLogin doctor_login = UserLogin.UserBuilder.aUser()
        .withIsAdmin(false)
        .withUsername(USERNAME_DOCTOR_EMPLOYEE)
        .withPassword(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private Employee doctor = Employee.EmployeeBuilder.anEmployee()
        .withUsername(USERNAME_DOCTOR_EMPLOYEE)
        .withName(NAME_DOCTOR_EMPLOYEE)
        .withBirthday(BIRTHDAY_DOCTOR_EMPLOYEE)
        .withType(TYPE_DOCTOR_EMPLOYEE)
        .withEmail(EMAIL_DOCTOR_EMPLOYEE)
        .build();

    private UserLogin janitor_login = UserLogin.UserBuilder.aUser()
        .withIsAdmin(false)
        .withUsername(USERNAME_JANITOR_EMPLOYEE)
        .withPassword(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private Employee janitor = Employee.EmployeeBuilder.anEmployee()
        .withUsername(USERNAME_JANITOR_EMPLOYEE)
        .withName(NAME_JANITOR_EMPLOYEE)
        .withBirthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .withType(TYPE_JANITOR_EMPLOYEE)
        .withEmail(EMAIL_JANITOR_EMPLOYEE)
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
    @Test
    public void filledRepository_combinedSearch_resultContainsSubstringAndType() {
        userDetailService.createNewUser(animal_caretaker_login);
        userDetailService.createNewUser(doctor_login);
        userDetailService.createNewUser(janitor_login);
        employeeService.createEmployee(anmial_caretaker);
        employeeService.createEmployee(doctor);
        employeeService.createEmployee(janitor);
        List<Employee> employees = employeeService.findByNameAndType(Employee.EmployeeBuilder.anEmployee().withType(EmployeeType.JANITOR).withName("aN").build());
        assertEquals(1, employees.size());
        assertEquals(employees.get(0).getUsername(), janitor.getUsername());
    }
}
