package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import org.mapstruct.Mapper;

@Mapper
public class UserMapper {
/*
    @Named("user")
    UserLoginDto userToUserDto(User user);

    ApplicationUser userToApplicationUser(User user);

 */
public UserLoginDto userLoginDto(UserLogin userLogin) {
    if(userLogin == null) {
        return null;
    }
    return UserLoginDto.builder()
        .username(userLogin.getUsername())
        .password(userLogin.getPassword())
        .build(); //bilo je i .password - greska??
}
}
