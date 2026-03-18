package naderdeghaili.capstoneproject.payloads.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PatientResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String fiscalCode,
        String email,
        String phone,
        String address,
        String photoUrl,
        Boolean emailConsent,
        LocalDateTime createdAt
) {
}