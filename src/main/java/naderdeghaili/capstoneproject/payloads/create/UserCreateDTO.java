package naderdeghaili.capstoneproject.payloads.create;

import jakarta.validation.constraints.*;
import naderdeghaili.capstoneproject.entities.UserType;

public record UserCreateDTO(
        @NotBlank(message = "Name is required") @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters") String firstName,

        @NotBlank(message = "Surname is required")
        @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is not in the correct format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
                message = "Password must contain at least one uppercase, one lowercase and one number")
        String password,

        @NotNull(message = "Role is required")
        UserType role) {

}
