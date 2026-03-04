package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.QuoteItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuoteItemRepository extends JpaRepository<QuoteItem, UUID> {
    Page<QuoteItem> findByQuote_Id(UUID id, Pageable pageable);

    Page<QuoteItem> findByProcedure_Id(UUID id, Pageable pageable);

}
