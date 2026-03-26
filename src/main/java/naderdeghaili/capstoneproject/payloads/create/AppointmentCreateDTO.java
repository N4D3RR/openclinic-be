package naderdeghaili.capstoneproject.payloads.create;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentCreateDTO(
        @NotNull(message = "Patient id is required")
        UUID patientId,

        @NotNull(message = "User id is required")
        UUID userId,

        UUID treatmentPlanId,

        @NotNull(message = "Date and time are required")
        @Future(message = "Appointment date must be in the future")
        LocalDateTime dateTime,

        @NotNull(message = "Duration is required")
        @Min(value = 15, message = "Duration must be at least 15 minutes")
        Integer duration,

        String notes
) {
}
