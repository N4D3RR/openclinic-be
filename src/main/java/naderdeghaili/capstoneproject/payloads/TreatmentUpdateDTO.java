package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record TreatmentUpdateDTO(@Positive(message = "Cost must be positive")
                                 Double cost,

                                 String notes,
                                 String imageUrl,

                                 @PastOrPresent(message = "Treatment date cannot be in the future")
                                 LocalDate date) {
}
