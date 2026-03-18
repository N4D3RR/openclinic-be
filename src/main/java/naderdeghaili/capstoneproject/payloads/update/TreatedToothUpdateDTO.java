package naderdeghaili.capstoneproject.payloads.update;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import naderdeghaili.capstoneproject.entities.ToothSurface;

public record TreatedToothUpdateDTO(
        @Min(value = 11, message = "Tooth code must be at least 11 (FDI notation)")
        @Max(value = 48, message = "Tooth code must be at most 48 (FDI notation)")
        Integer toothCode,
        ToothSurface surface) {
}
