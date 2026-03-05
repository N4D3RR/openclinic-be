package naderdeghaili.capstoneproject.payloads;

import naderdeghaili.capstoneproject.entities.DocumentType;

public record DocumentUpdateDTO(
        String fileName,
        String fileUrl,
        DocumentType type,
        String notes
) {
}
