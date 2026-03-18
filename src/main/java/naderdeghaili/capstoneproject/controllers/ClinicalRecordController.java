package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.create.ClinicalRecordCreateDTO;
import naderdeghaili.capstoneproject.payloads.responses.ClinicalRecordResponseDTO;
import naderdeghaili.capstoneproject.payloads.update.ClinicalRecordUpdateDTO;
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
    private final DTOMapper mapper;

    public ClinicalRecordController(ClinicalRecordService clinicalRecordService, DTOMapper mapper) {
        this.clinicalRecordService = clinicalRecordService;
        this.mapper = mapper;
    }

    //GET ALL - api/clinical-records
    @GetMapping
    public Page<ClinicalRecordResponseDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return this.clinicalRecordService.getAll(page, size).map(mapper::toClinicalRecordDTO);
    }

    //GET BY ID - api/clinical-records/{id}
    @GetMapping("/{id}")
    public ClinicalRecordResponseDTO getById(@PathVariable UUID id) {
        return mapper.toClinicalRecordDTO(this.clinicalRecordService.findById(id));
    }

    //GET BY PATIENT - api/clinical-records/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public ClinicalRecordResponseDTO getByPatient(@PathVariable UUID patientId) {
        return mapper.toClinicalRecordDTO(this.clinicalRecordService.findByPatient(patientId));
    }

    //POST - api/clinical-records
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicalRecordResponseDTO create(@RequestBody @Validated ClinicalRecordCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toClinicalRecordDTO(this.clinicalRecordService.saveClinicalRecord(payload));
    }

    //UPDATE - api/clinical-records/{id}
    @PutMapping("/{id}")
    public ClinicalRecordResponseDTO update(@PathVariable UUID id,
                                            @RequestBody @Validated ClinicalRecordUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toClinicalRecordDTO(this.clinicalRecordService.findByIdAndUpdate(id, payload));
    }

    //DELETE - api/clinical-records/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        this.clinicalRecordService.findByIdAndDelete(id);
    }
}
