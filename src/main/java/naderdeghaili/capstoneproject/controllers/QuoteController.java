package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.QuoteStatus;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.create.QuoteCreateDTO;
import naderdeghaili.capstoneproject.payloads.responses.QuoteResponseDTO;
import naderdeghaili.capstoneproject.payloads.update.QuoteUpdateDTO;
import naderdeghaili.capstoneproject.services.QuotePdfService;
import naderdeghaili.capstoneproject.services.QuoteService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/quotes")

public class QuoteController {

    private final QuoteService quoteService;
    private final DTOMapper mapper;
    private final QuotePdfService quotePdfService;

    public QuoteController(QuoteService quoteService, DTOMapper mapper, QuotePdfService quotePdfService) {
        this.quoteService = quoteService;
        this.mapper = mapper;
        this.quotePdfService = quotePdfService;
    }

    //GET ALL - api/quotes
    // ADMIN vede tutto, DENTIST solo i propri
    @GetMapping
    public Page<QuoteResponseDTO> getAll(@AuthenticationPrincipal User currentUser,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return quoteService.getAll(currentUser, page, size).map(mapper::toQuoteDTO);
    }

    //GET BY ID - api/quotes/{quoteId}
    @GetMapping("/{quoteId}")
    public QuoteResponseDTO getById(@PathVariable UUID quoteId,
                                    @AuthenticationPrincipal User currentUser) {
        return mapper.toQuoteDTO(this.quoteService.findById(quoteId, currentUser));
    }

    //GET BY PATIENT- api/quotes/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<QuoteResponseDTO> getByPatient(@PathVariable UUID patientId,
                                               @AuthenticationPrincipal User currentUser,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        return this.quoteService.findByPatient(patientId, currentUser, page, size).map(mapper::toQuoteDTO);
    }

    //GET BY STATUS - api/quotes/status
    @GetMapping("/status")
    public Page<QuoteResponseDTO> getByStatus(@RequestParam QuoteStatus status,
                                              @AuthenticationPrincipal User currentUser,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return this.quoteService.findByStatus(status, currentUser, page, size).map(mapper::toQuoteDTO);

    }

    //POST api/quotes
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST')")
    public QuoteResponseDTO create(@AuthenticationPrincipal User currentUser,
                                   @RequestBody @Validated QuoteCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toQuoteDTO(this.quoteService.saveQuote(payload, currentUser));
    }

    //PUT - api/quotes/{quoteId}
    @PutMapping("/{quoteId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST')")
    public QuoteResponseDTO update(@PathVariable UUID quoteId,
                                   @AuthenticationPrincipal User currentUser,
                                   @RequestBody @Validated QuoteUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toQuoteDTO(this.quoteService.findByIdAndUpdate(quoteId, payload, currentUser));

    }

    //DELETE - api/quotes/{quoteId}
    @DeleteMapping("/{quoteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST')")
    public void delete(@PathVariable UUID quoteId,
                       @AuthenticationPrincipal User currentUser) {
        this.quoteService.findByIdAndDelete(quoteId, currentUser);
    }

    //GET PDF QUOTE - api/quotes/{id}/pdf
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        byte[] pdf = quotePdfService.generatePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"preventivo-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    
    @GetMapping("/kpi")
    public Map<String, Object> getKpi() {
        return quoteService.getKpi();
    }
}
