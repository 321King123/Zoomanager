package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeMapper() {
    }

    public EmployeeDto employeeToEmployeeDto(Employee employee) {
        if(employee == null) {
            return null;
        }
        return EmployeeDto.builder()
            .username(employee.getUsername())
            .name(employee.getName())
            .birthday(employee.getBirthday())
            .email(employee.getEmail())
            .type(employee.getType())
            .password("").build();
    }

    public Employee employeeDtoToEmployee(EmployeeDto employeeDto) {
        if(employeeDto == null) {
            return null;
        }
        return Employee.builder()
            .name(employeeDto.getName())
            .username(employeeDto.getUsername())
            .birthday(employeeDto.getBirthday())
            .email(employeeDto.getEmail())
            .type(employeeDto.getType()).build();
    }

}
