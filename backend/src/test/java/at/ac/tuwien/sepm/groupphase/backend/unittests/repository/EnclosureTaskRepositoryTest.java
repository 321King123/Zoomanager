package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class EnclosureTaskRepositoryTest {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserLoginRepository userLoginRepository;

    @Autowired
    EnclosureRepository enclosureRepository;

    @Autowired
    EnclosureTaskRepository enclosureTaskRepository;

    private final UserLogin animal_caretaker_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private final Employee anmial_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .build();

    private Task task_not_assigned = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.NOT_ASSIGNED)
        .build();

    private Task task_assigned = Task.builder()
        .id(null)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .build();

    private Task task_assigned2 = Task.builder()
        .id(null)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .build();

    private Task task_assigned3 = Task.builder()
        .id(null)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .build();

    private Enclosure barn = Enclosure.builder()
        .name("Barn")
        .build();

    @BeforeEach
    public void beforeEach() {
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
    }

    @Test
    public void givenValidTaskAndEnclosure_createEnclosureTask() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);

//        EnclosureTask enclosureTask = enclosureTaskRepository.save(EnclosureTask.builder()
//            .id(createdTask.getId())
//            .subject(enclosure)
//            .build());

        enclosureTaskRepository.save(EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build());

        EnclosureTask et = enclosureTaskRepository.findById(createdTask.getId()).get();

        Task ot = taskRepository.getOne(et.getId());
        //Enclosure ec = enclosureTaskRepository.getTaskSubjectById(createdTask.getId());

        assertEquals(et.getSubject(), enclosure);
        assertEquals(ot, createdTask);
    }
}
