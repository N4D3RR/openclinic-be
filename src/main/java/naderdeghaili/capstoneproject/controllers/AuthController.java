package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.LoginDTO;
import naderdeghaili.capstoneproject.payloads.LoginResDTO;
import naderdeghaili.capstoneproject.services.AuthService;
import naderdeghaili.capstoneproject.services.UserService;
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
    private final UserService userService;
    private final DTOMapper mapper;


    public AuthController(AuthService authService, UserService userService, DTOMapper mapper) {
        this.authService = authService;
        this.userService = userService;
        this.mapper = mapper;
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

//    POST /auth/register lo lascio in UserController
//    @PostMapping("/register")
//    @ResponseStatus(HttpStatus.CREATED)
//    public UserResponseDTO register(@RequestBody @Validated UserCreateDTO payload, BindingResult validation) {
//        if (validation.hasErrors())
//            throw new ValidationException(validation.getAllErrors().stream()
//                    .map(e -> e.getDefaultMessage()).toList());
//
//        return mapper.toUserDTO(this.userService.saveUser(payload));
//    }
}
