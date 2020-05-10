package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.Objects;

@Entity
public class Employee {


   // @OneToOne(targetEntity = User.class,mappedBy = "username")
    @Id
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date birthday;

    @Column(nullable = false)
    private String /*Workertype*/ type;

    @Column(nullable = false)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(username, employee.username) &&
            Objects.equals(name, employee.name) &&
            Objects.equals(birthday, employee.birthday) &&
            Objects.equals(type, employee.type) &&
            Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, birthday, type, email);
    }

    @Override
    public String toString() {
        return "Employee{" +
            "username='" + username + '\'' +
            ", name='" + name + '\'' +
            ", birthday=" + birthday +
            ", type='" + type + '\'' +
            ", email='" + email + '\'' +
            '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
