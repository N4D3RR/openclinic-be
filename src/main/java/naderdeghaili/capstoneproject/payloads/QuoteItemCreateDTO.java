package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteItemCreateDTO(
        @NotNull(message = "Quote id is required")
        UUID quoteId,

        @NotNull(message = "Procedure id is required")
        UUID procedureId,

        @NotNull(message = "Tooth number is required")
        @Min(value = 11, message = "Tooth code must be at least 11 (FDI notation)")
        @Max(value = 48, message = "Tooth code must be at most 48 (FDI notation)")
        Integer toothNumber,

        @NotNull(message = "Quoted price is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal quotedPrice
) {
}
