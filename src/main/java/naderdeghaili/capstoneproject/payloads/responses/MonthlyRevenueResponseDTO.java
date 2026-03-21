package naderdeghaili.capstoneproject.payloads.responses;

import java.math.BigDecimal;

public record MonthlyRevenueResponseDTO(int year, int month, BigDecimal total) {
}