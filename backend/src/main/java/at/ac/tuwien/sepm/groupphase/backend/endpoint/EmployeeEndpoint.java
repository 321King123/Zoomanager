package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserLoginMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EmployeeService employeeService;
    private final UserService userService;
    private final EmployeeMapper employeeMapper;
    private final UserLoginMapper userLoginMapper;

    @Autowired
    public EmployeeEndpoint(EmployeeService employeeService,UserService userService,
                            EmployeeMapper employeeMapper, UserLoginMapper userLoginMapper){
        this.employeeService=employeeService;
        this.userService=userService;
        this.employeeMapper=employeeMapper;
        this.userLoginMapper=userLoginMapper;
    }


    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "")
    @ApiOperation(value = "Create new employee", authorizations = {@Authorization(value = "apiKey")})
    public EmployeeDto createEmployee(@RequestBody @Valid EmployeeDto employeeDto){
        LOGGER.info("POST /api/v1/employee body: {}",employeeDto);
        userService.createNewUser(userLoginMapper.eployeeDtoToUserLogin(employeeDto));
        return employeeMapper.employeeToEmployeeDto(employeeService.createEmployee(employeeMapper.employeeDtoToEmployee(employeeDto)));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/listofemployees")
    public List<EmployeeDto> getAllEmployees(){
        LOGGER.info("GET /api/v1/employee/listofemployees");
        List<Employee> employees = employeeService.getAll();
        List<EmployeeDto> employeeDtos = new LinkedList<>();
        for(Employee e: employees){
            employeeDtos.add(employeeMapper.employeeToEmployeeDto(e));
        }
        return employeeDtos;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/search")
    public List<EmployeeDto> searchEmployees(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "type", required = false) EmployeeType type){
        LOGGER.info("GET /api/v1/employee/search");
        Employee searchEmployee = Employee.EmployeeBuilder.anEmployee().withName(name).withType(type).build();
        List<Employee> employees = employeeService.findByNameAndType(searchEmployee);
        List<EmployeeDto> employeeDtos = new LinkedList<>();
        for(Employee e: employees){
            employeeDtos.add(employeeMapper.employeeToEmployeeDto(e));
        }
        return employeeDtos;
    }

}
