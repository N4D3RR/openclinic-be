package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import naderdeghaili.capstoneproject.entities.UserType;

public record UserUpdateDTO(@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
                            String firstName,

                            @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
                            String lastName,

                            @Email(message = "Email is not in the correct format")
                            String email,

                            @Size(min = 8, message = "Password must be at least 8 characters")
                            @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
                                    message = "Password must contain at least one uppercase, one lowercase and one number")
                            String password,

                            UserType role) {
}
