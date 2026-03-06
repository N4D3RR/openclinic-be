package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.QuoteItem;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.QuoteItemCreateDTO;
import naderdeghaili.capstoneproject.payloads.QuoteItemUpdateDTO;
import naderdeghaili.capstoneproject.services.QuoteItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/quote-items")
@PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST')")
public class QuoteItemController {

    private final QuoteItemService quoteItemService;


    public QuoteItemController(QuoteItemService quoteItemService) {
        this.quoteItemService = quoteItemService;
    }

    //GET ALL - api/quote-items
    //TODO: valutare se lasciarlo
    @GetMapping
    public Page<QuoteItem> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return this.quoteItemService.getAll(page, size);
    }

    //GET BY ID - api/quote-items/{quoteItemId}
    @GetMapping("/{quoteItemId}")
    public QuoteItem getById(@PathVariable UUID quoteItemId) {

        return this.quoteItemService.findById(quoteItemId);
    }

    //GET BY QUOTE - api/quote-items/{quoteId}
    @GetMapping("/quote/{quoteId}")
    public Page<QuoteItem> getByQuote(@PathVariable UUID quoteId,
                                      @AuthenticationPrincipal User currentUser,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {

        return this.quoteItemService.findByQuote(quoteId, currentUser, page, size);
    }

    //POST - api/quote-items
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuoteItem save(@AuthenticationPrincipal User currentUser,
                          @RequestBody @Validated QuoteItemCreateDTO payload, BindingResult validation) {

        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList());

        return this.quoteItemService.saveQuoteItem(payload, currentUser);
    }

    //PUT - api/quote-items/{quoteItemId}
    @PutMapping("/{quoteItemId}")
    public QuoteItem update(@PathVariable UUID quoteItemId,
                            @AuthenticationPrincipal User currentUser,
                            @RequestBody @Validated QuoteItemUpdateDTO payload,
                            BindingResult validation) {

        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList());

        return this.quoteItemService.findByIdAndUpdate(quoteItemId, payload, currentUser);
    }

    //DELETE - api/quote-items/{quoteItemId}
    @DeleteMapping("/{quoteItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID quoteItemId,
                       @AuthenticationPrincipal User currentUser) {

        this.quoteItemService.findByIdAndDelete(quoteItemId, currentUser);
    }
}
