package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record TreatmentCreateDTO(@NotNull(message = "Appointment id is required")
                                 UUID appointmentId,

                                 @NotNull(message = "Procedure id is required")
                                 UUID procedureId,

                                 @NotNull(message = "Cost is required")
                                 @Positive(message = "Cost must be positive")
                                 Double cost,

                                 String notes,
                                 String imageUrl,

                                 @NotNull(message = "Date is required")
                                 @PastOrPresent(message = "Treatment date cannot be in the future")
                                 LocalDate date) {
}
