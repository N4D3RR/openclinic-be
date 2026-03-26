package naderdeghaili.capstoneproject.payloads.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import naderdeghaili.capstoneproject.entities.PaymentMethod;
import naderdeghaili.capstoneproject.entities.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentCreateDTO(@NotNull(message = "Patient id is required")
                               UUID patientId,

                               UUID appointmentId,

                               @NotNull(message = "Amount is required")
                               @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
                               BigDecimal amount,

                               @NotNull(message = "Payment date is required")
                               @PastOrPresent(message = "Payment date cannot be in the future")
                               LocalDate paymentDate,

                               @NotNull(message = "Payment method is required")
                               PaymentMethod method,

                               String notes,

                               @NotNull
                               PaymentStatus status
) {

}