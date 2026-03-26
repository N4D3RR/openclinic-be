package naderdeghaili.capstoneproject.payloads.responses;

import naderdeghaili.capstoneproject.entities.PaymentMethod;
import naderdeghaili.capstoneproject.entities.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDTO(UUID id,
                                 BigDecimal amount,
                                 LocalDate paymentDate,
                                 PaymentMethod method,
                                 PaymentStatus status,
                                 String notes,
                                 LocalDateTime createdAt,
                                 PatientResponseDTO patient,
                                 UUID appointmentId) {
}
