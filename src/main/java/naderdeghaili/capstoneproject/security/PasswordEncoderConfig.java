package naderdeghaili.capstoneproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//classe separata per evitare dipendenza circolare securityconfig- password encoder- securityconfig

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder getBCrypt() {

        return new BCryptPasswordEncoder(12);
    }

}
