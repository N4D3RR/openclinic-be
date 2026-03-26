package naderdeghaili.capstoneproject.payloads.responses;

import naderdeghaili.capstoneproject.entities.UserType;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        UserType role,
        LocalDateTime createdAt
) {
}