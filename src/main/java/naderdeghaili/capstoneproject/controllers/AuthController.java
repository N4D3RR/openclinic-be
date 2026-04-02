package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.login.LoginDTO;
import naderdeghaili.capstoneproject.payloads.login.LoginResDTO;
import naderdeghaili.capstoneproject.services.AuthService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;

    }

    // POST /auth/login
    @PostMapping("/login")
    public LoginResDTO login(@RequestBody @Validated LoginDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        String token = this.authService.authenticateUserAndGenerateToken(payload);
        return new LoginResDTO(token);
    }

}
