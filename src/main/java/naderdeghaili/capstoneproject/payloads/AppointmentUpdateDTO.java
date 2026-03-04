package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import naderdeghaili.capstoneproject.entities.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentUpdateDTO(
        @Future(message = "Appointment date must be in the future")
        LocalDateTime dateTime,

        @Min(value = 15, message = "Duration must be at least 15 minutes")
        Integer duration,

        AppointmentStatus status,
        String notes
) {
}
