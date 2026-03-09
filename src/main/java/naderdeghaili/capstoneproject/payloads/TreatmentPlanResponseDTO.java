package naderdeghaili.capstoneproject.payloads;

import naderdeghaili.capstoneproject.entities.TreatmentPlanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TreatmentPlanResponseDTO(UUID id,
                                       TreatmentPlanStatus status,
                                       LocalDate startDate,
                                       LocalDate expectedEndDate,
                                       String clinicalNotes,
                                       BigDecimal totalAmount,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt,
                                       QuoteResponseDTO quote,
                                       List<AppointmentResponseDTO> appointments) {
}
