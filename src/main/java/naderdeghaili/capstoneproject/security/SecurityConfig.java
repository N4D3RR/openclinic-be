package naderdeghaili.capstoneproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain sfc(HttpSecurity httpSecurity) {
        //disabilito login form
        httpSecurity.formLogin(fl -> fl.disable());
        //disabilito controllo sicurezza csrf
        httpSecurity.csrf(csrf -> csrf.disable());
        //lavoriamo in modalità stateless
        httpSecurity.sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //ilbera tutti
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(
                "/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api-docs/**",
                "/api-docs.yaml"
        ).permitAll());


        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder getBCrypt() {
        return new BCryptPasswordEncoder(12);
    }


}