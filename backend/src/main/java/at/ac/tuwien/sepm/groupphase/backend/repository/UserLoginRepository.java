package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<User, String> {

    /**
     * Finds a user with a specific username
     *
     * @param username of the user to be found
     * @return user with the corresponding username
     */
    User findUserByUsername(String username);

}
