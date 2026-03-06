package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.TreatmentPlan;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.TreatmentPlanUpdateDTO;
import naderdeghaili.capstoneproject.services.TreatmentPlanService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/treatment-plans")
@PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST', 'HYGIENIST')")
public class TreatmentPlanController {

    private final TreatmentPlanService treatmentPlanService;

    public TreatmentPlanController(TreatmentPlanService treatmentPlanService) {
        this.treatmentPlanService = treatmentPlanService;
    }

    //GET ALL - api/treatment-plans
    @GetMapping
    public Page<TreatmentPlan> getAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {

        return this.treatmentPlanService.getAll(page, size);
    }

    //GET BY ID  - api/treatment-plans/{treatmentPlanId}
    @GetMapping("/{treatmentPlanId}")
    public TreatmentPlan getById(@PathVariable UUID treatmentPlanId) {

        return this.treatmentPlanService.findById(treatmentPlanId);
    }

    //GET BY PATIENT - api/treatment-plans/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<TreatmentPlan> getByPatient(@PathVariable UUID patientId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return this.treatmentPlanService.findByPatient(patientId, page, size);
    }

    //GET BY QUOTE - api/treatment-plans/quote/{quoteId}
    @GetMapping("/quote/{quoteId}")
    public TreatmentPlan getByQuote(@PathVariable UUID quoteId) {

        return this.treatmentPlanService.findByQuote(quoteId);
    }

    //POST - viene eseguita automaticamente quando eseguo un PUT del Quote
    //quando passa da DRAFT ad ACCEPTED viene creato il treatmentplan con createFromQuote

    //PUT - api/treatment-plans/{treatmentPlanId}
    @PutMapping("/{treatmentPlanId}")
    public TreatmentPlan update(@PathVariable UUID treatmentPlanId,
                                @RequestBody @Validated TreatmentPlanUpdateDTO payload,
                                BindingResult validation) {

        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList());

        return this.treatmentPlanService.findByIdAndUpdate(treatmentPlanId, payload);
    }

    //DELETE - api/treatment-plans/{treatmentPlanId}
    @DeleteMapping("/{treatmentPlanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable UUID treatmentPlanId) {

        this.treatmentPlanService.findByIdAndDelete(treatmentPlanId);
    }

}
