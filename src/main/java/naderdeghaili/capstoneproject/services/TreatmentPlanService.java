package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.*;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.update.TreatmentPlanUpdateDTO;
import naderdeghaili.capstoneproject.repositories.TreatmentPlanRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class TreatmentPlanService {

    private final TreatmentPlanRepository treatmentPlanRepository;
    private final AppointmentService appointmentService;

    public TreatmentPlanService(TreatmentPlanRepository treatmentPlanRepository,
                                @Lazy AppointmentService appointmentService) {
        this.treatmentPlanRepository = treatmentPlanRepository;
        this.appointmentService = appointmentService;
    }

    // GET ALL
    public Page<TreatmentPlan> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatmentPlanRepository.findAll(pageable);
    }

    // GET BY ID
    public TreatmentPlan findById(UUID planId) {
        return treatmentPlanRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("TreatmentPlan with id " + planId + " not found"));
    }

    // GET BY PATIENT
    public Page<TreatmentPlan> findByPatient(UUID patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatmentPlanRepository.findByQuote_Patient_Id(patientId, pageable);
    }

    // GET BY QUOTE
    public TreatmentPlan findByQuote(UUID quoteId) {
        return treatmentPlanRepository.findByQuote_Id(quoteId)
                .orElseThrow(() -> new NotFoundException("TreatmentPlan for quote " + quoteId + " not found"));
    }

    // CREATE FROM QUOTE — in QuoteService quando status diventa ACCEPTED viene creato il treatment plan
    public TreatmentPlan createFromQuote(Quote quote) {
        BigDecimal totalAmount = quote.getItems().stream()
                .map(QuoteItem::getQuotedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //durata dell trattamento
        int totalMinutes = quote.getItems().stream()
                .mapToInt(item -> item.getProcedure().getDurationInMinutes())
                .sum();
        
        //calcolo data di fine come 1 settimana per ogni 60min di trattamenti, minimo 1 settimana
        LocalDate expectedEndDate = LocalDate.now().plusWeeks(Math.max(1, totalMinutes / 60));

        TreatmentPlan plan = new TreatmentPlan(
                quote,
                TreatmentPlanStatus.IN_PROGRESS,
                expectedEndDate,
                null,
                totalAmount
        );
        plan.setStartDate(LocalDate.now());

        log.info("TreatmentPlan created for quote " + quote.getId());
        return treatmentPlanRepository.save(plan);
    }

    // ADD APPOINTMENT TO PLAN
    public TreatmentPlan addAppointment(UUID planId, UUID appointmentId) {
        TreatmentPlan plan = this.findById(planId);
        Appointment appointment = appointmentService.findById(appointmentId);
        plan.addAppointment(appointment);
        log.info("Appointment " + appointmentId + " added to TreatmentPlan " + planId);
        return treatmentPlanRepository.save(plan);
    }

    // UPDATE
    public TreatmentPlan findByIdAndUpdate(UUID id, TreatmentPlanUpdateDTO payload) {
        TreatmentPlan found = this.findById(id);

        if (payload.status() != null) found.setStatus(payload.status());
        if (payload.expectedEndDate() != null) found.setExpectedEndDate(payload.expectedEndDate());
        if (payload.clinicalNotes() != null) found.setClinicalNotes(payload.clinicalNotes());

        log.info("TreatmentPlan with id " + id + " updated successfully");
        return treatmentPlanRepository.save(found);
    }

    // DELETE
    public void findByIdAndDelete(UUID id) {
        TreatmentPlan found = this.findById(id);
        treatmentPlanRepository.delete(found);
        log.info("TreatmentPlan with id " + id + " deleted successfully");
    }

    public boolean existsByQuoteId(UUID quoteId) {
        return treatmentPlanRepository.existsByQuote_Id(quoteId);
    }

}
