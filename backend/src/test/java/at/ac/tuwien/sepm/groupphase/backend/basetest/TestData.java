package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.*;

public interface TestData {


    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";
    String ANIMAL_BASE_URI = BASE_URI + "/animals";
    String EMPLOYEE_BASE_URI = BASE_URI + "/employee";
    String TASK_BASE_URI = BASE_URI + "/tasks";

    String USERNAME_ANIMAL_CARE_EMPLOYEE = "AnimalCarer";
    String NAME_ANIMAL_CARE_EMPLOYEE = "AnimalCarerName";
    Date BIRTHDAY_ANIMAL_CARE_EMPLOYEE = new Date(2000,Calendar.JANUARY,1);
    EmployeeType TYPE_ANIMAL_CARE_EMPLOYEE = EmployeeType.ANIMAL_CARE;
    String EMAIL_ANIMAL_CARE_EMPLOYEE = "animalcare@email.com";
    List<Animal> ASSIGNED_ANIMALS_ANIMAL_CARE_EMPLOYEE = new LinkedList<>();

    String USERNAME_DOCTOR_EMPLOYEE = "Doctor";
    String NAME_DOCTOR_EMPLOYEE = "DoctorName";
    Date BIRTHDAY_DOCTOR_EMPLOYEE = new Date(2000,Calendar.JANUARY,1);
    EmployeeType TYPE_DOCTOR_EMPLOYEE = EmployeeType.DOCTOR;
    String EMAIL_DOCTOR_EMPLOYEE = "doctor@email.com";

    String USERNAME_JANITOR_EMPLOYEE = "Janitor";
    String NAME_JANITOR_EMPLOYEE = "JanitorName";
    Date BIRTHDAY_JANITOR_EMPLOYEE = new Date(2000,Calendar.JANUARY,1);
    EmployeeType TYPE_JANITOR_EMPLOYEE = EmployeeType.JANITOR;
    String EMAIL_JANITOR_EMPLOYEE = "janitor@email.com";

    Long ANIMAL_ID = 1L;
    String ANIMAL_NAME_HORSE = "Horse";
    String ANIMAL_DESCRIPTION_FAST = "Fast";
    String ANIMAL_SPECIES_ARABIAN = "ARABIAN";
    String ANIMAL_PUBLIC_INFORMATION_FAMOUS = "famous";


    String VALID_TEST_PASSWORD = "Password1";


    String TASK_TITLE = "Tasktitle";

    String TASK_DESCRIPTION = "Taskdescription";

    LocalDateTime TAST_START_TIME = LocalDateTime.of(2030, 1, 1, 12, 30);

    LocalDateTime TAST_END_TIME = LocalDateTime.of(2030, 1, 1, 13, 30);

    String TASK_ASSIGNED_EMPLOYEE_USERNAME = USERNAME_ANIMAL_CARE_EMPLOYEE;

    TaskStatus TASK_STATUS = TaskStatus.ASSIGNED;

    String ADMIN_USER = "admin";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "user";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

}
