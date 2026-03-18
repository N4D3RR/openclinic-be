package naderdeghaili.capstoneproject.services;

import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.exceptions.UnauthorizedException;
import naderdeghaili.capstoneproject.payloads.login.LoginDTO;
import naderdeghaili.capstoneproject.security.JWTTools;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JWTTools jwtTools;
    private final PasswordEncoder bcrypt;

    public AuthService(UserService userService, JWTTools jwtTools, PasswordEncoder bcrypt) {
        this.userService = userService;
        this.jwtTools = jwtTools;
        this.bcrypt = bcrypt;
    }

    public String authenticateUserAndGenerateToken(LoginDTO payload) {
        User user = userService.findByEmail(payload.email());

        if (!bcrypt.matches(payload.password(), user.getPassword()))
            throw new UnauthorizedException("Invalid credentials");

        return jwtTools.createToken(user);
    }
}
