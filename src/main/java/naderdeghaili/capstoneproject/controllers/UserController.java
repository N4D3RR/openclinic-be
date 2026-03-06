package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.entities.UserType;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.UserCreateDTO;
import naderdeghaili.capstoneproject.payloads.UserUpdateDTO;
import naderdeghaili.capstoneproject.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //GET /api/users/me - utente loggato
    @GetMapping("/me")
    public User getMe(@AuthenticationPrincipal User currentUser) {
        return currentUser;
    }

    //GET ALL /api/users
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<User> getAll(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        return userService.getAll(page, size);
    }

    //GET BY ID /api/users/{userId}
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getById(@PathVariable UUID userId) {
        return userService.findByID(userId);
    }

    //GET BY ROLE /api/users/role
    @GetMapping("/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<User> getByRole(@RequestParam UserType role,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return userService.findByRole(role, page, size);
    }

    //POST /api/users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public User create(@RequestBody @Validated UserCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return userService.saveUser(payload);
    }

    //PUT /api/users/{userId}
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User update(@PathVariable UUID userId,
                       @RequestBody @Validated UserUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return userService.findByIdAndUpdate(userId, payload);
    }


    //DELETE /api/users/{userId}
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable UUID userId) {
        userService.findByIdAndDelete(userId);
    }
}
