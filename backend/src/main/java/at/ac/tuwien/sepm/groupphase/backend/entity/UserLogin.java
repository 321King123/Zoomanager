package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class UserLogin {

    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isAdmin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLogin user = (UserLogin) o;
        return isAdmin == user.isAdmin &&
            Objects.equals(username, user.username) &&
            Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, isAdmin);
    }

    @Override
    public String toString() {
        return "User{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", isAdmin=" + isAdmin +
            '}';
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static final class UserBuilder {
        private String username;
        private boolean isAdmin;
        private String password;

        private UserBuilder() {
        }

        public static UserLogin.UserBuilder aUser() {
            return new UserLogin.UserBuilder();
        }

        public UserLogin.UserBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserLogin.UserBuilder withIsAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public UserLogin.UserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }


        public UserLogin build() {
            UserLogin user = new UserLogin();
            user.setUsername(username);
            user.setAdmin(isAdmin);
            user.setPassword(password);
            return user;
        }
    }
}
