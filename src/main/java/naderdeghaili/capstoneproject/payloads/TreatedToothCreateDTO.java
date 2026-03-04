package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.NotNull;
import naderdeghaili.capstoneproject.entities.ToothSurface;

import java.util.UUID;

public record TreatedToothCreateDTO(@NotNull(message = "Treatment id is required")
                                    UUID treatmentId,

                                    @NotNull(message = "Tooth code is required")
                                    Integer toothCode,

                                    ToothSurface surface) {
}
