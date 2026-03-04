package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Quote;
import naderdeghaili.capstoneproject.entities.QuoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuotesRepository extends JpaRepository<Quote, UUID> {
    Page<Quote> findByPatient_Id(UUID id, Pageable pageable);

    Page<Quote> findByDentist_Id(UUID id, Pageable pageable);

    Page<Quote> findByStatus(QuoteStatus status, Pageable pageable);

    Page<Quote> findByPatient_IdAndStatus(UUID id, QuoteStatus status, Pageable pageable);


}
