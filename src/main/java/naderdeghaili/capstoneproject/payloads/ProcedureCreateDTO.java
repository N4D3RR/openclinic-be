package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.*;

public record ProcedureCreateDTO(
        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        String description,

        @NotNull(message = "Duration is required")
        @Min(value = 5, message = "Duration must be at least 5 minutes")
        Integer durationInMinutes,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double price
) {
}
