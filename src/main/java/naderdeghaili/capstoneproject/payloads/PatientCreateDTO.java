package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientCreateDTO(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @NotBlank(message = "Fiscal code is required")
        @Size(min = 16, max = 16, message = "Fiscal code must be exactly 16 characters")
        String fiscalCode,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is not in the correct format")
        String email,

        @NotBlank(message = "Phone is required")
        String phone,

        String address
) {
}
