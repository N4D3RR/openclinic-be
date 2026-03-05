package naderdeghaili.capstoneproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain sfc(HttpSecurity httpSecurity, JWTCheckerFilter jwtCheckerFilter) {
        //disabilito login form
        httpSecurity.formLogin(fl -> fl.disable());
        //disabilito controllo sicurezza csrf
        httpSecurity.csrf(csrf -> csrf.disable());
        //lavoriamo in modalità stateless
        httpSecurity.sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/api-docs.yaml").permitAll()
                .anyRequest().authenticated()
        );

        httpSecurity.addFilterBefore(jwtCheckerFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


}