package naderdeghaili.capstoneproject.payloads.update;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TreatmentUpdateDTO(@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
                                 BigDecimal cost,

                                 String notes,
                                 String imageUrl,

                                 @PastOrPresent(message = "Treatment date cannot be in the future")
                                 LocalDate date) {
}
