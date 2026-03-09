package naderdeghaili.capstoneproject.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProcedureUpdateDTO(@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
                                 String name,

                                 String description,

                                 @Min(value = 5, message = "Duration must be at least 5 minutes")
                                 Integer durationInMinutes,

                                 @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
                                 BigDecimal price) {
}
