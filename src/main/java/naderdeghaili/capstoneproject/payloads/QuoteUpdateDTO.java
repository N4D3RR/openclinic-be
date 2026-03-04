package naderdeghaili.capstoneproject.payloads;

import naderdeghaili.capstoneproject.entities.QuoteStatus;

public record QuoteUpdateDTO(
        QuoteStatus status,
        String notes) {
}
