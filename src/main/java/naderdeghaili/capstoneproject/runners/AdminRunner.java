package naderdeghaili.capstoneproject.runners;


import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.entities.UserType;
import naderdeghaili.capstoneproject.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//creo utente admin al primo avvio se non c'è
//TODO: rimuovere o spostare in env i dati prima del deploy
@Component
public class AdminRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public AdminRunner(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@test.it")) {
            User admin = new User(
                    "Nader",
                    "Test",
                    "admin@test.it",
                    encoder.encode("Admin1234"),
                    UserType.ADMIN
            );
            userRepository.save(admin);
            System.out.println("Admin user created");
        }
    }
}