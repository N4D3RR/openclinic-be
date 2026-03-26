package naderdeghaili.capstoneproject.payloads.update;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import naderdeghaili.capstoneproject.entities.PaymentMethod;
import naderdeghaili.capstoneproject.entities.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentUpdateDTO(
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @PastOrPresent(message = "Payment date cannot be in the future")
        LocalDate paymentDate,

        PaymentMethod method,
        PaymentStatus status,
        String notes
) {

}