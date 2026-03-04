package naderdeghaili.capstoneproject.payloads;

import naderdeghaili.capstoneproject.entities.ToothSurface;

public record TreatedToothUpdateDTO(
        Integer toothCode,
        ToothSurface surface) {
}
