package naderdeghaili.capstoneproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//separate class to break SecurityConfig -> UserService -> PasswordEncoder circular dependency

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder getBCrypt() {

        return new BCryptPasswordEncoder(12);
    }

}
