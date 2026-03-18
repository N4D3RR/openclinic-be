package naderdeghaili.capstoneproject.payloads.create;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClinicalRecordCreateDTO(@NotNull(message = "Patient id is required")
                                      UUID patientId,

                                      String anamnesis,
                                      String allergies,
                                      String medications,
                                      String notes) {
}
