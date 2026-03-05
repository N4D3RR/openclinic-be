package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.LoginDTO;
import naderdeghaili.capstoneproject.payloads.LoginResDTO;
import naderdeghaili.capstoneproject.payloads.UserCreateDTO;
import naderdeghaili.capstoneproject.services.AuthService;
import naderdeghaili.capstoneproject.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // POST /auth/login
    @PostMapping("/login")
    public LoginResDTO login(@RequestBody @Validated LoginDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        String token = authService.authenticateUserAndGenerateToken(payload);
        return new LoginResDTO(token);
    }

    // POST /auth/register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody @Validated UserCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return userService.saveUser(payload);
    }
}
