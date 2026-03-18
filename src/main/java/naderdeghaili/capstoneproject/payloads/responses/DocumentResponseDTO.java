package naderdeghaili.capstoneproject.payloads.responses;

import naderdeghaili.capstoneproject.entities.DocumentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponseDTO(UUID id,
                                  String fileName,
                                  String fileUrl,
                                  DocumentType type,
                                  String notes,
                                  LocalDateTime uploadedAt,
                                  UUID clinicalRecordId) {
}
