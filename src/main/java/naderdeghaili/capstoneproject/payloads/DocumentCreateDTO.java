package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import naderdeghaili.capstoneproject.entities.DocumentType;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record DocumentCreateDTO(
        @NotNull(message = "Clinical record id is required")
        UUID clinicalRecordId,

        @NotBlank(message = "File name is required")
        String fileName,

        @NotBlank(message = "File URL is required")
        @URL
        String fileUrl,

        @NotNull(message = "Document type is required")
        DocumentType type,

        String notes) {
}
