package naderdeghaili.capstoneproject.payloads.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import naderdeghaili.capstoneproject.entities.ToothSurface;

import java.util.UUID;

public record TreatedToothCreateDTO(@NotNull(message = "Treatment id is required")
                                    UUID treatmentId,

                                    @NotNull(message = "Tooth code is required")
                                    @Min(value = 11, message = "Tooth code must be at least 11 (FDI notation)")
                                    @Max(value = 48, message = "Tooth code must be at most 48 (FDI notation)")
                                    Integer toothCode,

                                    ToothSurface surface) {
}
