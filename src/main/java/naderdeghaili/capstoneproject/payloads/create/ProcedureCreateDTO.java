package naderdeghaili.capstoneproject.payloads.create;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

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
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal price
) {
}
