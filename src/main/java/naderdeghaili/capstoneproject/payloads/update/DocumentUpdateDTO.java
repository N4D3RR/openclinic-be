package naderdeghaili.capstoneproject.payloads.update;

import naderdeghaili.capstoneproject.entities.DocumentType;

public record DocumentUpdateDTO(
        String fileName,
        String fileUrl,
        DocumentType type,
        String notes
) {
}
