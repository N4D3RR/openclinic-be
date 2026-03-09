package naderdeghaili.capstoneproject.payloads;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ClinicalRecordResponseDTO(UUID id,
                                        String anamnesis,
                                        String allergies,
                                        String medications,
                                        Boolean signedConsent,
                                        String notes,
                                        LocalDateTime createdAt,
                                        LocalDateTime updatedAt,
                                        PatientResponseDTO patient,
                                        List<DocumentResponseDTO> documents) {
}
