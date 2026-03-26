package naderdeghaili.capstoneproject.payloads.update;

import naderdeghaili.capstoneproject.entities.QuoteStatus;

public record QuoteUpdateDTO(
        QuoteStatus status,
        String notes) {
}
