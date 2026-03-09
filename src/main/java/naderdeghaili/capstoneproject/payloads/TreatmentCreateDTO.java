package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TreatmentCreateDTO(@NotNull(message = "Appointment id is required")
                                 UUID appointmentId,

                                 @NotNull(message = "Procedure id is required")
                                 UUID procedureId,

                                 @NotNull(message = "Cost is required")
                                 @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
                                 BigDecimal cost,

                                 String notes,
                                 String imageUrl,

                                 @NotNull(message = "Date is required")
                                 @PastOrPresent(message = "Treatment date cannot be in the future")
                                 LocalDate date) {
}
