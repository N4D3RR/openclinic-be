package naderdeghaili.capstoneproject.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private List<String> errorsMessages;

    public ValidationException(List<String> errorsMessages) {

        super("Validation errors in payload");
        this.errorsMessages = errorsMessages;
    }
}