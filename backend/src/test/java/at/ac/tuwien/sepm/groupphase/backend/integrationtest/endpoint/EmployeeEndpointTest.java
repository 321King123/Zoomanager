package at.ac.tuwien.sepm.groupphase.backend.integrationtest.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EmployeeEndpointTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    //for testing assignment
    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    String GET_FILTERED_EMPLOYEES_URI = EMPLOYEE_BASE_URI + "/search";

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
        animal_caretaker_login = UserLogin.UserBuilder.aUser()
            .withIsAdmin(false)
            .withUsername(USERNAME_ANIMAL_CARE_EMPLOYEE)
            .withPassword(passwordEncoder.encode(VALID_TEST_PASSWORD))
            .build();

        anmial_caretaker = Employee.EmployeeBuilder.anEmployee()
            .withUsername(USERNAME_ANIMAL_CARE_EMPLOYEE)
            .withName(NAME_ANIMAL_CARE_EMPLOYEE)
            .withBirthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
            .withType(TYPE_ANIMAL_CARE_EMPLOYEE)
            .withEmail(EMAIL_ANIMAL_CARE_EMPLOYEE)
            .build();

        doctor_login = UserLogin.UserBuilder.aUser()
            .withIsAdmin(false)
            .withUsername(USERNAME_DOCTOR_EMPLOYEE)
            .withPassword(passwordEncoder.encode(VALID_TEST_PASSWORD))
            .build();

        doctor = Employee.EmployeeBuilder.anEmployee()
            .withUsername(USERNAME_DOCTOR_EMPLOYEE)
            .withName(NAME_DOCTOR_EMPLOYEE)
            .withBirthday(BIRTHDAY_DOCTOR_EMPLOYEE)
            .withType(TYPE_DOCTOR_EMPLOYEE)
            .withEmail(EMAIL_DOCTOR_EMPLOYEE)
            .build();

        janitor_login = UserLogin.UserBuilder.aUser()
            .withIsAdmin(false)
            .withUsername(USERNAME_JANITOR_EMPLOYEE)
            .withPassword(passwordEncoder.encode(VALID_TEST_PASSWORD))
            .build();

        janitor = Employee.EmployeeBuilder.anEmployee()
            .withUsername(USERNAME_JANITOR_EMPLOYEE)
            .withName(NAME_JANITOR_EMPLOYEE)
            .withBirthday(BIRTHDAY_JANITOR_EMPLOYEE)
            .withType(TYPE_JANITOR_EMPLOYEE)
            .withEmail(EMAIL_JANITOR_EMPLOYEE)
            .build();
    }

    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get(EMPLOYEE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));

        assertEquals(0, employeeDtos.size());
    }

    @Test
    public void repositoryWithAllTypes_whenFindDoctor_thenOnlyDoctorInList() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?type=DOCTOR")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));
        assertEquals(1, employeeDtos.size());
        assertEquals(employeeDtos.get(0).getType(), EmployeeType.DOCTOR);
        assertEquals(employeeDtos.get(0).getUsername(), doctor.getUsername());
    }

    @Test
    public void repositoryWithAllTypes_whenFindAnimalCaretaker_thenOnlyAnimalCaretakerInList() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?type=ANIMAL_CARE")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));
        assertEquals(1, employeeDtos.size());
        assertEquals(employeeDtos.get(0).getType(), EmployeeType.ANIMAL_CARE);
        assertEquals(employeeDtos.get(0).getUsername(), anmial_caretaker.getUsername());
    }

    @Test
    public void repositoryWithAllTypes_whenFindJanitor_thenOnlyJanitorInList() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?type=JANITOR")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));
        assertEquals(1, employeeDtos.size());
        assertEquals(employeeDtos.get(0).getType(), EmployeeType.JANITOR);
        assertEquals(employeeDtos.get(0).getUsername(), janitor.getUsername());
    }

    @Test
    public void repositoryWithAllTypes_whenFindAll_thenReturnAll() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(EMPLOYEE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));
        assertEquals(3, employeeDtos.size());
        //all three should be different
        assertNotSame(employeeDtos.get(0), employeeDtos.get(1));
        assertNotSame(employeeDtos.get(1), employeeDtos.get(2));
        assertNotSame(employeeDtos.get(0), employeeDtos.get(2));
    }

    @Test
    public void filledRepository_whenSearchName_thenReturnAllWithSubstringNameCaseInsensitive() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?name=aN")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));

        assertEquals(2, employeeDtos.size());
        assertNotSame(employeeDtos.get(0), employeeDtos.get(1));
    }

    @Test
    public void filledRepository_whenCombinedSearchNameType_thenReturnAllWithSubstringNameCaseInsensitiveTypeJanitor() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?name=aN&type=JANITOR")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));

        assertEquals(1, employeeDtos.size());
        assertNotSame(employeeDtos.get(0).getUsername(), janitor.getUsername());
    }

    @Test
    public void filledRepository_whenSearchWithNoMatch_thenReturnNotFoundException() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?name=notInRepository")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        assertEquals(response.getContentAsString(), "No employee fits the given criteria");
    }
}
