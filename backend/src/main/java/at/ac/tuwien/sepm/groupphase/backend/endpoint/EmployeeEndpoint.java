package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserLoginMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;

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
    public EmployeeDto createEmployee(@RequestBody EmployeeDto employeeDto){
        LOGGER.info("POST /api/v1/employee body: {}",employeeDto);
        userService.createNewUser(userLoginMapper.eployeeDtoToUserLogin(employeeDto));
        return employeeMapper.employeeToEmployeeDto(employeeService.createEmployee(employeeMapper.employeeDtoToEmployee(employeeDto)));
    }

}
