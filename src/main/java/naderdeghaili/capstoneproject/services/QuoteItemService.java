package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Procedure;
import naderdeghaili.capstoneproject.entities.Quote;
import naderdeghaili.capstoneproject.entities.QuoteItem;
import naderdeghaili.capstoneproject.entities.QuoteStatus;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.QuoteItemCreateDTO;
import naderdeghaili.capstoneproject.payloads.QuoteItemUpdateDTO;
import naderdeghaili.capstoneproject.repositories.QuoteItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class QuoteItemService {

    private final QuoteItemRepository quoteItemRepository;
    private final QuoteService quoteService;
    private final ProcedureService procedureService;

    public QuoteItemService(QuoteItemRepository quoteItemRepository, QuoteService quoteService,
                            ProcedureService procedureService) {
        this.quoteItemRepository = quoteItemRepository;
        this.quoteService = quoteService;
        this.procedureService = procedureService;
    }

    // GET ALL
    public Page<QuoteItem> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return quoteItemRepository.findAll(pageable);
    }

    // GET BY ID
    public QuoteItem findById(UUID quoteItemId) {
        return quoteItemRepository.findById(quoteItemId)
                .orElseThrow(() -> new NotFoundException("QuoteItem with id " + quoteItemId + " not found"));
    }

    // GET BY QUOTE
    public Page<QuoteItem> findByQuote(UUID quoteId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return quoteItemRepository.findByQuote_Id(quoteId, pageable);
    }

    // SAVE
    public QuoteItem saveQuoteItem(QuoteItemCreateDTO payload) {
        Quote quote = quoteService.findById(payload.quoteId());

        if (quote.getStatus() != QuoteStatus.DRAFT)
            throw new IllegalArgumentException("Can only add items to a DRAFT quote");

        Procedure procedure = procedureService.findById(payload.procedureId());

        QuoteItem quoteItem = new QuoteItem(
                payload.toothNumber(),
                payload.quotedPrice(),
                quote,
                procedure
        );

        quote.addItem(quoteItem);

        log.info("QuoteItem added to quote " + quote.getId());
        return quoteItemRepository.save(quoteItem);
    }

    // UPDATE
    public QuoteItem findByIdAndUpdate(UUID id, QuoteItemUpdateDTO payload) {
        QuoteItem found = this.findById(id);

        if (found.getQuote().getStatus() != QuoteStatus.DRAFT)
            throw new IllegalArgumentException("Can only modify items of a DRAFT quote");

        if (payload.toothNumber() != null) found.setToothNumber(payload.toothNumber());
        if (payload.quotedPrice() != null) found.setQuotedPrice(payload.quotedPrice());

        log.info("QuoteItem with id " + id + " updated successfully");
        return quoteItemRepository.save(found);
    }

    // DELETE
    public void findByIdAndDelete(UUID id) {
        QuoteItem found = this.findById(id);

        if (found.getQuote().getStatus() != QuoteStatus.DRAFT)
            throw new IllegalArgumentException("Can only delete items from a DRAFT quote");

        quoteItemRepository.delete(found);
        log.info("QuoteItem with id " + id + " deleted successfully");
    }
}
