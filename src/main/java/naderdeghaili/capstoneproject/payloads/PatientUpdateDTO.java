package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientUpdateDTO(@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
                               String firstName,

                               @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
                               String lastName,

                               @Past(message = "Birth date must be in the past")
                               LocalDate birthDate,

                               @Size(min = 16, max = 16, message = "Fiscal code must be exactly 16 characters")
                               String fiscalCode,

                               @Email(message = "Email is not in the correct format")
                               String email,

                               String phone,
                               String address,
                               Boolean emailConsent) {
}
