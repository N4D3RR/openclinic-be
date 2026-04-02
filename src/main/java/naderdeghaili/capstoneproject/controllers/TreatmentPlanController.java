package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.responses.TreatmentPlanResponseDTO;
import naderdeghaili.capstoneproject.payloads.update.TreatmentPlanUpdateDTO;
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
    private final DTOMapper mapper;

    public TreatmentPlanController(TreatmentPlanService treatmentPlanService, DTOMapper mapper) {
        this.treatmentPlanService = treatmentPlanService;
        this.mapper = mapper;
    }

    //GET ALL - api/treatment-plans
    @GetMapping
    public Page<TreatmentPlanResponseDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {

        return this.treatmentPlanService.getAll(page, size).map(mapper::toTreatmentPlanDTO);
    }

    //GET BY ID  - api/treatment-plans/{treatmentPlanId}
    @GetMapping("/{treatmentPlanId}")
    public TreatmentPlanResponseDTO getById(@PathVariable UUID treatmentPlanId) {

        return mapper.toTreatmentPlanDTO(treatmentPlanService.findById(treatmentPlanId));
    }

    //GET BY PATIENT - api/treatment-plans/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<TreatmentPlanResponseDTO> getByPatient(@PathVariable UUID patientId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return this.treatmentPlanService.findByPatient(patientId, page, size).map(mapper::toTreatmentPlanDTO);
    }

    //GET BY QUOTE - api/treatment-plans/quote/{quoteId}
    @GetMapping("/quote/{quoteId}")
    public TreatmentPlanResponseDTO getByQuote(@PathVariable UUID quoteId) {

        return mapper.toTreatmentPlanDTO(this.treatmentPlanService.findByQuote(quoteId));
    }

    //POST - auto-created when a Quote transitions to ACCEPTED (no manual endpoint)


    //PUT - api/treatment-plans/{treatmentPlanId}
    @PutMapping("/{treatmentPlanId}")
    public TreatmentPlanResponseDTO update(@PathVariable UUID treatmentPlanId,
                                           @RequestBody @Validated TreatmentPlanUpdateDTO payload,
                                           BindingResult validation) {

        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList());

        return mapper.toTreatmentPlanDTO(this.treatmentPlanService.findByIdAndUpdate(treatmentPlanId, payload));
    }

    //DELETE - api/treatment-plans/{treatmentPlanId}
    @DeleteMapping("/{treatmentPlanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable UUID treatmentPlanId) {

        this.treatmentPlanService.findByIdAndDelete(treatmentPlanId);
    }

}
