package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.Positive;

public record QuoteItemUpdateDTO(
        Integer toothNumber,

        @Positive(message = "Quoted price must be positive")
        Double quotedPrice
) {
}
