package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.ClinicalRecord;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.ClinicalRecordCreateDTO;
import naderdeghaili.capstoneproject.payloads.ClinicalRecordUpdateDTO;
import naderdeghaili.capstoneproject.services.ClinicalRecordService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/clinical-records")
@PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST', 'HYGIENIST')")
public class ClinicalRecordController {

    private final ClinicalRecordService clinicalRecordService;

    public ClinicalRecordController(ClinicalRecordService clinicalRecordService) {
        this.clinicalRecordService = clinicalRecordService;
    }

    @GetMapping
    public Page<ClinicalRecord> getAll(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return clinicalRecordService.getAll(page, size);
    }

    @GetMapping("/{id}")
    public ClinicalRecord getById(@PathVariable UUID id) {
        return clinicalRecordService.findById(id);
    }

    @GetMapping("/patient/{patientId}")
    public ClinicalRecord getByPatient(@PathVariable UUID patientId) {
        return clinicalRecordService.findByPatient(patientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicalRecord create(@RequestBody @Validated ClinicalRecordCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return clinicalRecordService.saveClinicalRecord(payload);
    }

    @PutMapping("/{id}")
    public ClinicalRecord update(@PathVariable UUID id,
                                 @RequestBody @Validated ClinicalRecordUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return clinicalRecordService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        clinicalRecordService.findByIdAndDelete(id);
    }
}
