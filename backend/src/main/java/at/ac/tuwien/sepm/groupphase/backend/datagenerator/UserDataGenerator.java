package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class UserDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserLoginRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataGenerator(UserLoginRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void generateMessage() {
        if (userRepository.findAll().size() > 0) {
            LOGGER.debug("user already generated");
        } else {
            LOGGER.debug("generating {} user entries", 2);

            UserLogin user = UserLogin.UserBuilder.aUser().withIsAdmin(true).withPassword(passwordEncoder.encode("password")).withUsername("admin").build();
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);
            user = UserLogin.UserBuilder.aUser().withIsAdmin(false).withPassword(passwordEncoder.encode("password")).withUsername("user").build();
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);

        }
    }
}
