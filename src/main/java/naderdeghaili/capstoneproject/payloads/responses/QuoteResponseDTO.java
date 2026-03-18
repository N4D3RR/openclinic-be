package naderdeghaili.capstoneproject.payloads.responses;

import naderdeghaili.capstoneproject.entities.QuoteStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuoteResponseDTO(UUID id,
                               QuoteStatus status,
                               String notes,
                               LocalDateTime createdAt,
                               PatientResponseDTO patient,
                               UserResponseDTO dentist,
                               List<QuoteItemResponseDTO> items,
                               UUID treatmentPlanId) {
}
