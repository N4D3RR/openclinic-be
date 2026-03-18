package naderdeghaili.capstoneproject.payloads.responses;

import naderdeghaili.capstoneproject.entities.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponseDTO(UUID id,
                                     LocalDateTime dateTime,
                                     Integer duration,
                                     AppointmentStatus status,
                                     String notes,
                                     LocalDateTime createdAt,
                                     PatientResponseDTO patient,
                                     UserResponseDTO dentist,
                                     UUID treatmentPlanId) {
}
