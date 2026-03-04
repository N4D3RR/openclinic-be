package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record QuoteCreateDTO(@NotNull(message = "Patient id is required")
                             UUID patientId,

                             @NotNull(message = "Dentist id is required")
                             UUID dentistId,

                             String notes) {
}
