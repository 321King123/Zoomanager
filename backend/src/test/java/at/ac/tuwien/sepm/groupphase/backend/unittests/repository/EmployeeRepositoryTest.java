package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserLoginRepository userLoginRepository;

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
    public void beforeEach(){
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
    }

    @AfterEach
    public void afterEach(){
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
    }

    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() {
        List<Employee> employees = employeeRepository.findAll();
        assertEquals(0, employees.size());
    }

    @Test
    public void filledRepository_whenFindAll_thenListOfAllEmployees() {
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        List<Employee> employees = employeeRepository.findAll();
        assertEquals(3, employees.size());
        assertTrue(employees.contains(janitor));
        assertTrue(employees.contains(anmial_caretaker));
        assertTrue(employees.contains(doctor));
    }
}
