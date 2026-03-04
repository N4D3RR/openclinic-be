package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProcedureUpdateDTO(@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
                                 String name,

                                 String description,

                                 @Min(value = 5, message = "Duration must be at least 5 minutes")
                                 Integer durationInMinutes,

                                 @Positive(message = "Price must be positive")
                                 Double price) {
}
