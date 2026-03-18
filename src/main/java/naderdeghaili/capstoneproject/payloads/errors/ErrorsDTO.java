package naderdeghaili.capstoneproject.payloads.errors;


import java.time.LocalDateTime;

public record ErrorsDTO(String message, LocalDateTime timeStamp) {
}
