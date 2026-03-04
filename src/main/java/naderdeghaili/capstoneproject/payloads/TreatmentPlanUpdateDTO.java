package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.Future;
import naderdeghaili.capstoneproject.entities.TreatmentPlanStatus;

import java.time.LocalDate;

public record TreatmentPlanUpdateDTO(

        TreatmentPlanStatus status,

        @Future(message = "Expected end date must be in the future")
        LocalDate expectedEndDate,

        String clinicalNotes
) {
}
