package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Quote;
import naderdeghaili.capstoneproject.entities.QuoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    Page<Quote> findByPatient_Id(UUID id, Pageable pageable);

    Page<Quote> findByDentist_Id(UUID id, Pageable pageable);

    Page<Quote> findByStatus(QuoteStatus status, Pageable pageable);

    Page<Quote> findByPatient_IdAndStatus(UUID id, QuoteStatus status, Pageable pageable);

    Page<Quote> findByPatient_IdAndDentist_Id(UUID patientId, UUID dentistId, Pageable pageable);

    Page<Quote> findByDentist_IdAndStatus(UUID dentistId, QuoteStatus status, Pageable pageable);

    @Query("SELECT COUNT(q) FROM Quote q WHERE q.status = :status")
    Long countByStatus(@Param("status") QuoteStatus status);

    @Query("SELECT COALESCE(SUM(i.quotedPrice), 0) FROM QuoteItem i WHERE i.quote.patient.id = :patientId")
    BigDecimal sumByPatient(@Param("patientId") UUID patientId);

    @Query("SELECT COUNT(q) FROM Quote q")
    Long countAll();
}
