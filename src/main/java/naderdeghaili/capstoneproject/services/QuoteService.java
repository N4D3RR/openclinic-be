package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.*;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.exceptions.UnauthorizedException;
import naderdeghaili.capstoneproject.payloads.create.QuoteCreateDTO;
import naderdeghaili.capstoneproject.payloads.update.QuoteUpdateDTO;
import naderdeghaili.capstoneproject.repositories.QuoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final PatientService patientService;
    private final UserService userService;
    private final TreatmentPlanService treatmentPlanService;

    public QuoteService(QuoteRepository quoteRepository, PatientService patientService,
                        UserService userService, TreatmentPlanService treatmentPlanService) {
        this.quoteRepository = quoteRepository;
        this.patientService = patientService;
        this.userService = userService;
        this.treatmentPlanService = treatmentPlanService;
    }

    //GET ALL — ADMIN can see any quote, DENTIST only their own
    @Transactional(readOnly = true)
    public Page<Quote> getAll(User currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (currentUser.getRole() == UserType.ADMIN || currentUser.getRole() == UserType.SECRETARY) {
            return quoteRepository.findAll(pageable);
        }
        return quoteRepository.findByDentist_Id(currentUser.getId(), pageable);
    }

    //GET BY ID — ADMIN can see any quote, DENTIST only their own
    @Transactional(readOnly = true)
    public Quote findById(UUID quoteId, User currentUser) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException("Quote with id " + quoteId + " not found"));
        checkOwnership(quote, currentUser);
        return quote;
    }

    //GET BY ID
    @Transactional(readOnly = true)
    public Quote findById(UUID quoteId) {
        return quoteRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException("Quote with id " + quoteId + " not found"));
    }

    //GET BY PATIENT
    @Transactional(readOnly = true)
    public Page<Quote> findByPatient(UUID patientId, User currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (currentUser.getRole() == UserType.ADMIN || currentUser.getRole() == UserType.SECRETARY) {
            return quoteRepository.findByPatient_Id(patientId, pageable);
        }
        return quoteRepository.findByPatient_IdAndDentist_Id(patientId, currentUser.getId(), pageable);
    }

    //GET BY DENTIST
    @Transactional(readOnly = true)
    public Page<Quote> findByDentist(UUID dentistId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return quoteRepository.findByDentist_Id(dentistId, pageable);
    }

    //GET BY STATUS - ADMIN can see any quote, DENTIST only their own
    @Transactional(readOnly = true)
    public Page<Quote> findByStatus(QuoteStatus status, User currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (currentUser.getRole() == UserType.ADMIN) {
            return quoteRepository.findByStatus(status, pageable);
        }
        return quoteRepository.findByDentist_IdAndStatus(currentUser.getId(), status, pageable);
    }

    //SAVE — authenticated DENTIST becomes quote dentist
    @Transactional
    public Quote saveQuote(QuoteCreateDTO payload, User currentUser) {
        Patient patient = patientService.findById(payload.patientId());

        // ADMIN can assign a different dentist; DENTIST creates for themselves
        User dentist;
        if (currentUser.getRole() == UserType.ADMIN && payload.dentistId() != null) {
            dentist = userService.findByID(payload.dentistId());
            if (dentist.getRole() != UserType.DENTIST)
                throw new IllegalArgumentException("Assigned user must be a DENTIST");
        } else {
            if (currentUser.getRole() != UserType.DENTIST)
                throw new IllegalArgumentException("Only a DENTIST can create quotes");
            dentist = currentUser;
        }

        Quote quote = new Quote(
                QuoteStatus.DRAFT,
                payload.notes(),
                patient,
                dentist
        );

        log.info("Quote for patient " + patient.getId() + " saved by " + currentUser.getId());
        return quoteRepository.save(quote);
    }

    //UPDATE — ADMIN can update any quote, DENTIST only their own
    @Transactional
    public Quote findByIdAndUpdate(UUID quoteId, QuoteUpdateDTO payload, User currentUser) {
        Quote found = this.findById(quoteId, currentUser);

        if (payload.notes() != null) found.setNotes(payload.notes());
        //on ACCEPTED status, auto-generate TreatmentPlan via createFromQuote
        if (payload.status() != null) {
            if (payload.status() == QuoteStatus.ACCEPTED && found.getStatus() != QuoteStatus.ACCEPTED) {
                if (found.getItems().isEmpty())
                    throw new IllegalArgumentException("Cannot accept a quote with no items");
                //avoid duplicate plans if selecting Accept multiple times
                if (treatmentPlanService.existsByQuoteId(found.getId()))
                    throw new IllegalStateException("A TreatmentPlan already exists for this quote");
                treatmentPlanService.createFromQuote(found);
            }
            found.setStatus(payload.status());
        }

        log.info("Quote with id " + quoteId + " updated by " + currentUser.getId());
        return quoteRepository.save(found);
    }

    //DELETE — ADMIN can delete anything, DENTIST only their own
    @Transactional
    public void findByIdAndDelete(UUID quoteId, User currentUser) {
        Quote found = this.findById(quoteId, currentUser);
        quoteRepository.delete(found);
        log.info("Quote with id " + quoteId + " deleted by " + currentUser.getId());
    }

    //GET QUOTES KPI
    public Map<String, Object> getKpi() {
        Map<String, Object> kpi = new HashMap<>();
        kpi.put("total", quoteRepository.countAll());
        kpi.put("draft", quoteRepository.countByStatus(QuoteStatus.DRAFT));
        kpi.put("sent", quoteRepository.countByStatus(QuoteStatus.SENT));
        kpi.put("accepted", quoteRepository.countByStatus(QuoteStatus.ACCEPTED));
        kpi.put("rejected", quoteRepository.countByStatus(QuoteStatus.REJECTED));
        return kpi;
    }

    //check that DENTIST only accesses his own quotes
    private void checkOwnership(Quote quote, User currentUser) {
        if (currentUser.getRole() != UserType.ADMIN
                && !quote.getDentist().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only access your own quotes");
        }
    }
}
