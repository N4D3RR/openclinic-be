package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Payment;
import naderdeghaili.capstoneproject.entities.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findByPatient_Id(UUID id, Pageable pageable);

    Page<Payment> findByAppointment_Id(UUID id, Pageable pageable);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    //patient debts
    List<Payment> findByPatient_IdAndStatus(UUID id, PaymentStatus status);

    //filter by period
    Page<Payment> findByPaymentDateBetween(LocalDate paymentDateStart, LocalDate paymentDateEnd, Pageable pageable);

    //filter by period + status
    Page<Payment> findByPaymentDateBetweenAndStatus(LocalDate paymentDateStart, LocalDate paymentDateEnd, PaymentStatus status, Pageable pageable);

    //sum amounts by status — COALESCE prevents null when no results exist
    @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p WHERE p.status = :status")
    BigDecimal sumByStatus(@Param("status") PaymentStatus status);

    //count payments by status
    @Query("SELECT COALESCE(COUNT(p),0) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") PaymentStatus status);

    //revenue for a specific month
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0) FROM Payment p
            WHERE p.status = :status
            AND EXTRACT(YEAR FROM p.paymentDate) = :year
            AND EXTRACT(MONTH FROM p.paymentDate) = :month
            """)
    BigDecimal revenueByYearMonth(@Param("status") PaymentStatus status, @Param("year") int year, @Param("month") int month);

    //monthly revenue aggregates (grouped by year/month, descending)
    @Query("SELECT EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate), SUM(p.amount) " +
            "FROM Payment p WHERE p.status = 'PAID' " +
            "GROUP BY EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate) " +
            "ORDER BY EXTRACT(YEAR FROM p.paymentDate) DESC, EXTRACT(MONTH FROM p.paymentDate) DESC")
    List<Object[]> monthlyRevenue();

    //progressive invoice numbering for PAID payments
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PAID' and EXTRACT(YEAR FROM p.paymentDate) = :year")
    Long countPaidByYear(@Param("year") int year);
}
