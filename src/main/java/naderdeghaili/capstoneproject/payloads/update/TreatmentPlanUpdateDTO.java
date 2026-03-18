package naderdeghaili.capstoneproject.payloads.update;

import jakarta.validation.constraints.FutureOrPresent;
import naderdeghaili.capstoneproject.entities.TreatmentPlanStatus;

import java.time.LocalDate;

public record TreatmentPlanUpdateDTO(

        TreatmentPlanStatus status,

        @FutureOrPresent(message = "Expected end date must be in the future")
        LocalDate expectedEndDate,

        String clinicalNotes
) {
}
