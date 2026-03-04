package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import naderdeghaili.capstoneproject.entities.PaymentMethod;
import naderdeghaili.capstoneproject.entities.PaymentStatus;

import java.time.LocalDate;

public record PaymentUpdateDTO(@NotNull(message = "Patient id is required")
                               @Positive(message = "Amount must be positive")
                               Double amount,

                               @PastOrPresent(message = "Payment date cannot be in the future")
                               LocalDate paymentDate,

                               PaymentMethod method,
                               PaymentStatus status,
                               String notes
) {

}