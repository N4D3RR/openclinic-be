package naderdeghaili.capstoneproject.payloads;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProcedureResponseDTO(
        UUID id,
        String code,
        String name,
        String description,
        Integer durationInMinutes,
        BigDecimal price,
        LocalDateTime createdAt
) {
}
