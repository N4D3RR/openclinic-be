package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import naderdeghaili.capstoneproject.entities.PaymentMethod;

import java.time.LocalDate;
import java.util.UUID;

public record PaymentCreateDTO(@NotNull(message = "Patient id is required")
                               UUID patientId,

                               UUID appointmentId,

                               @NotNull(message = "Amount is required")
                               @Positive(message = "Amount must be positive")
                               Double amount,

                               @NotNull(message = "Payment date is required")
                               @PastOrPresent(message = "Payment date cannot be in the future")
                               LocalDate paymentDate,

                               @NotNull(message = "Payment method is required")
                               PaymentMethod method,

                               String notes
) {

}