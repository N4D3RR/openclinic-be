package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record QuoteItemCreateDTO(
        @NotNull(message = "Quote id is required")
        UUID quoteId,

        @NotNull(message = "Procedure id is required")
        UUID procedureId,

        @NotNull(message = "Tooth number is required")
        Integer toothNumber,

        @NotNull(message = "Quoted price is required")
        @Positive(message = "Quoted price must be positive")
        Double quotedPrice
) {
}
