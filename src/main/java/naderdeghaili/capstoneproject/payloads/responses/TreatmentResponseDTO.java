package naderdeghaili.capstoneproject.payloads.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TreatmentResponseDTO(UUID id,
                                   LocalDate date,
                                   BigDecimal cost,
                                   String notes,
                                   String imageUrl,
                                   ProcedureResponseDTO procedure,
                                   List<TreatedToothResponseDTO> treatedToothList,
                                   UUID appointmentId) {
}
