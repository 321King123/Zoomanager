package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import org.mapstruct.Mapper;

@Mapper
public interface UserLoginMapper {

    UserLogin userLoginDtoToUserLogin(UserLoginDto userLoginDto);

    UserLogin eployeeDtoToUserLogin(EmployeeDto employeeDto);

}
