package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record QuoteCreateDTO(@NotNull(message = "Patient id is required")
                             UUID patientId,

                             UUID dentistId,

                             String notes) {
}
