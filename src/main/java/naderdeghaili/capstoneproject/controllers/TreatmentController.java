package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.Treatment;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.TreatmentCreateDTO;
import naderdeghaili.capstoneproject.payloads.TreatmentUpdateDTO;
import naderdeghaili.capstoneproject.services.TreatmentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/treatments")
@PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST', 'HYGIENIST')")
public class TreatmentController {

    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    //GET ALL - /api/treatments
    @GetMapping
    public Page<Treatment> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return this.treatmentService.getAll(page, size);
    }


    //GET BY ID  - /api/treatments/{treatmentId}
    @GetMapping("/{treatmentId}")
    public Treatment getById(@PathVariable UUID treatmentId) {

        return this.treatmentService.findById(treatmentId);
    }

    //GET BY APPOINTMENT - /api/treatments/{appointmentId}
    @GetMapping("/appointment/{appointmentId}")
    public Page<Treatment> getByAppointment(@PathVariable UUID appointmentId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {

        return this.treatmentService.findByAppointment(appointmentId, page, size);
    }

    //GET BY PATIENT - /api/treatments/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<Treatment> getByPatient(@PathVariable UUID patientId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {

        return this.treatmentService.findByAppointment(patientId, page, size);
    }

    //GET BY PROCEDURE - /api/treatments/{procedureId}
    @GetMapping("/procedure/{procedureId}")
    public Page<Treatment> getByProcedure(@PathVariable UUID procedureId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {

        return this.treatmentService.findByProcedure(procedureId, page, size);
    }

    //POST - /api/treatments/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Treatment save(@RequestBody @Validated TreatmentCreateDTO payload, BindingResult validation) {

        if (validation.hasErrors())
            throw new ValidationException(validation
                    .getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .toList());

        return this.treatmentService.saveTreatment(payload);
    }

    //PUT - /api/treatments/{treatmentId}
    @PutMapping("/{treatmentId}")
    public Treatment update(@PathVariable UUID treatmentId,
                            @RequestBody @Validated TreatmentUpdateDTO payload,
                            BindingResult validation) {

        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList());

        return this.treatmentService.findByIdAndUpdate(treatmentId, payload);
    }

    //DELETE  - /api/treatments/{treatmentId}
    @DeleteMapping("/{treatmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID treatmentId) {

        this.treatmentService.findByIdAndDelete(treatmentId);
    }
}
