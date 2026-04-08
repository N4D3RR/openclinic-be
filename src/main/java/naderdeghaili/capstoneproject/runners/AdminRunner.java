package naderdeghaili.capstoneproject.runners;


import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.entities.UserType;
import naderdeghaili.capstoneproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//create admin at first boot if admin is not present
@Slf4j
@Component
public class AdminRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Value("${ADMIN_DEFAULT_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_DEFAULT_PASSWORD}")
    private String adminPassword;

    public AdminRunner(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                    "Admin",
                    "OpenClinic",
                    adminEmail,
                    encoder.encode(adminPassword),
                    UserType.ADMIN
            );
            userRepository.save(admin);
            log.info("Admin user created");
        }
    }
}