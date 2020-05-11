package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

public class EmployeeDto {

    @NotNull(message = "Username must not be null")
    private String username;

    @Email
    private String email;

    @NotNull(message = "Password must not be null")
    private String password;

    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "Birthdate must not be null")
    private Date birthday;

    @NotNull(message = "Type must not be null")
    private EmployeeType type;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public EmployeeType getType() {
        return type;
    }

    public void setType(EmployeeType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeDto that = (EmployeeDto) o;
        return Objects.equals(username, that.username) &&
            email.equals(that.email) &&
            Objects.equals(password, that.password) &&
            Objects.equals(name, that.name) &&
            Objects.equals(birthday, that.birthday) &&
            type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, password, name, birthday, type);
    }

    @Override
    public String toString() {
        return "EmployeeDto{" +
            "username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", name='" + name + '\'' +
            ", birthday=" + birthday +
            ", type=" + type +
            '}';
    }


    public static final class EmployeeDtoBuilder {
        private String username;
        private String email;
        private String password;
        private String name;
        private Date birthday;
        private EmployeeType type;

        private EmployeeDtoBuilder() {
        }

        public static EmployeeDtoBuilder anEmployeeDto() {
            return new EmployeeDtoBuilder();
        }

        public EmployeeDtoBuilder withUsername(@NotNull(message = "Username must not be null") String val) {
            username = val;
            return this;
        }

        public EmployeeDtoBuilder withEmail(@Email String val) {
            email = val;
            return this;
        }

        public EmployeeDtoBuilder withPassword(@NotNull(message = "Password must not be null") String val) {
            password = val;
            return this;
        }

        public EmployeeDtoBuilder withName(@NotNull(message = "Name must not be null") String val) {
            name = val;
            return this;
        }

        public EmployeeDtoBuilder withBirthday(@NotNull(message = "Birthdate must not be null") Date val) {
            birthday = val;
            return this;
        }

        public EmployeeDtoBuilder withType(@NotNull(message = "Type must not be null") EmployeeType val) {
            type = val;
            return this;
        }

        public EmployeeDto build() {
            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setUsername(username);
            employeeDto.setEmail(email);
            employeeDto.setPassword(password);
            employeeDto.setName(name);
            employeeDto.setBirthday(birthday);
            employeeDto.setType(type);
            return employeeDto;
        }
    }
}
