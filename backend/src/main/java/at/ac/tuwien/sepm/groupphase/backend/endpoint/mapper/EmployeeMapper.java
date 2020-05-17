package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeMapper(){}

    public EmployeeDto employeeToEmployeeDto(Employee employee){
        return EmployeeDto.EmployeeDtoBuilder.anEmployeeDto()
            .withUsername(employee.getUsername())
            .withName(employee.getName())
            .withBirthday(employee.getBirthday())
            .withEmail(employee.getEmail())
            .withType(employee.getType())
            .withPassword("").build();
    }

    public Employee employeeDtoToEmployee(EmployeeDto employeeDto){
        return Employee.EmployeeBuilder.anEmployee()
            .withName(employeeDto.getName())
            .withUsername(employeeDto.getUsername())
            .withBirthday(employeeDto.getBirthday())
            .withEmail(employeeDto.getEmail())
            .withType(employeeDto.getType()).build();
    }

}
