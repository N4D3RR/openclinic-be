package naderdeghaili.capstoneproject.payloads.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Email is not in the correct format")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
