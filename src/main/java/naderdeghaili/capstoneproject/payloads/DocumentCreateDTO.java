package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import naderdeghaili.capstoneproject.entities.DocumentType;

import java.util.UUID;

public record DocumentCreateDTO(
        @NotNull(message = "Clinical record id is required")
        UUID clinicalRecordId,

        @NotBlank(message = "File name is required")
        String fileName,

        @NotBlank(message = "File URL is required")
        String fileUrl,

        @NotNull(message = "Document type is required")
        DocumentType type,

        String notes) {
}
