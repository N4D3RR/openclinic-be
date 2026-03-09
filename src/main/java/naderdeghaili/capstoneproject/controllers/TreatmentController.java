package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.TreatmentCreateDTO;
import naderdeghaili.capstoneproject.payloads.TreatmentResponseDTO;
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
    private final DTOMapper mapper;

    public TreatmentController(TreatmentService treatmentService, DTOMapper mapper) {
        this.treatmentService = treatmentService;
        this.mapper = mapper;
    }

    //GET ALL - /api/treatments
    @GetMapping
    public Page<TreatmentResponseDTO> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return this.treatmentService.getAll(page, size).map(mapper::toTreatmentDTO);
    }


    //GET BY ID  - /api/treatments/{treatmentId}
    @GetMapping("/{treatmentId}")
    public TreatmentResponseDTO getById(@PathVariable UUID treatmentId) {

        return mapper.toTreatmentDTO(this.treatmentService.findById(treatmentId));
    }

    //GET BY APPOINTMENT - /api/treatments/{appointmentId}
    @GetMapping("/appointment/{appointmentId}")
    public Page<TreatmentResponseDTO> getByAppointment(@PathVariable UUID appointmentId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {

        return this.treatmentService.findByAppointment(appointmentId, page, size).map(mapper::toTreatmentDTO);
    }

    //GET BY PATIENT - /api/treatments/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<TreatmentResponseDTO> getByPatient(@PathVariable UUID patientId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {

        return this.treatmentService.findByPatient(patientId, page, size).map(mapper::toTreatmentDTO);
    }

    //GET BY PROCEDURE - /api/treatments/{procedureId}
    @GetMapping("/procedure/{procedureId}")
    public Page<TreatmentResponseDTO> getByProcedure(@PathVariable UUID procedureId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {

        return this.treatmentService.findByProcedure(procedureId, page, size).map(mapper::toTreatmentDTO);
    }

    //POST - /api/treatments/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreatmentResponseDTO save(@RequestBody @Validated TreatmentCreateDTO payload, BindingResult validation) {

        if (validation.hasErrors())
            throw new ValidationException(validation
                    .getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .toList());

        return mapper.toTreatmentDTO(this.treatmentService.saveTreatment(payload));
    }

    //PUT - /api/treatments/{treatmentId}
    @PutMapping("/{treatmentId}")
    public TreatmentResponseDTO update(@PathVariable UUID treatmentId,
                                       @RequestBody @Validated TreatmentUpdateDTO payload,
                                       BindingResult validation) {

        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList());

        return mapper.toTreatmentDTO(this.treatmentService.findByIdAndUpdate(treatmentId, payload));
    }

    //DELETE  - /api/treatments/{treatmentId}
    @DeleteMapping("/{treatmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID treatmentId) {

        this.treatmentService.findByIdAndDelete(treatmentId);
    }
}
