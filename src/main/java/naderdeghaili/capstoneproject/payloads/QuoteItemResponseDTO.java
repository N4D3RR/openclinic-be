package naderdeghaili.capstoneproject.payloads;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteItemResponseDTO(UUID id,
                                   Integer toothNumber,
                                   BigDecimal quotedPrice,
                                   ProcedureResponseDTO procedure,
                                   UUID quoteId) {
}
