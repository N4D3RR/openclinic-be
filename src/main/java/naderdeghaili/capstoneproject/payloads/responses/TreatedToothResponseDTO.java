package naderdeghaili.capstoneproject.payloads.responses;

import naderdeghaili.capstoneproject.entities.ToothSurface;

import java.util.UUID;

public record TreatedToothResponseDTO(UUID id,
                                      Integer toothCode,
                                      ToothSurface surface,
                                      UUID treatmentId) {
}
