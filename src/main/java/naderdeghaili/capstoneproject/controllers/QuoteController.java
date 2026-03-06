package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.Quote;
import naderdeghaili.capstoneproject.entities.QuoteStatus;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.QuoteCreateDTO;
import naderdeghaili.capstoneproject.payloads.QuoteUpdateDTO;
import naderdeghaili.capstoneproject.services.QuoteService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/quotes")
@PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST')")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    //GET ALL - api/quotes
    // ADMIN vede tutto, DENTIST solo i propri
    @GetMapping
    public Page<Quote> getAll(@AuthenticationPrincipal User currentUser,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        return quoteService.getAll(currentUser, page, size);
    }

    //GET BY ID - api/quotes/{quoteId}
    @GetMapping("/{quoteId}")
    public Quote getById(@PathVariable UUID quoteId,
                         @AuthenticationPrincipal User currentUser) {
        return quoteService.findById(quoteId, currentUser);
    }

    //GET BY PATIENT- api/quotes/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<Quote> getByPatient(@PathVariable UUID patientId,
                                    @AuthenticationPrincipal User currentUser,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return quoteService.findByPatient(patientId, currentUser, page, size);
    }

    //GET BY STATUS - api/quotes/status
    @GetMapping("/status")
    public Page<Quote> getByStatus(@RequestParam QuoteStatus status,
                                   @AuthenticationPrincipal User currentUser,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return quoteService.findByStatus(status, currentUser, page, size);
    }

    //POST api/quotes
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Quote create(@AuthenticationPrincipal User currentUser,
                        @RequestBody @Validated QuoteCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return quoteService.saveQuote(payload, currentUser);
    }

    //PUT - api/quotes/{quoteId}
    @PutMapping("/{quoteId}")
    public Quote update(@PathVariable UUID quoteId,
                        @AuthenticationPrincipal User currentUser,
                        @RequestBody @Validated QuoteUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return quoteService.findByIdAndUpdate(quoteId, payload, currentUser);
    }

    //DELETE - api/quotes/{quoteId}
    @DeleteMapping("/{quoteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID quoteId,
                       @AuthenticationPrincipal User currentUser) {
        quoteService.findByIdAndDelete(quoteId, currentUser);
    }
}
