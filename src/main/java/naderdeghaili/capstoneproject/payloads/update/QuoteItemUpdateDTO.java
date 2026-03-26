package naderdeghaili.capstoneproject.payloads.update;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record QuoteItemUpdateDTO(
        @Min(value = 11, message = "Tooth code must be at least 11 (FDI notation)")
        @Max(value = 48, message = "Tooth code must be at most 48 (FDI notation)")
        Integer toothNumber,

        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal quotedPrice
) {
}
