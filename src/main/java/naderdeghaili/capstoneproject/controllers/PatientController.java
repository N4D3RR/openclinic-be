package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.create.PatientCreateDTO;
import naderdeghaili.capstoneproject.payloads.responses.PatientResponseDTO;
import naderdeghaili.capstoneproject.payloads.update.PatientUpdateDTO;
import naderdeghaili.capstoneproject.services.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;
    private final DTOMapper mapper;


    public PatientController(PatientService patientService, DTOMapper mapper) {
        this.patientService = patientService;
        this.mapper = mapper;
    }

    //GET ALL /api/patients
    @GetMapping

    public Page<PatientResponseDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return this.patientService.getAll(page, size).map(mapper::toPatientDTO);
    }

    //GET BY ID /api/patients/{patientId}
    @GetMapping("/{patientId}")
    public PatientResponseDTO getById(@PathVariable UUID patientId) {
        return mapper.toPatientDTO(this.patientService.findById(patientId));
    }

    // GET BY LASTNAME /api/patients/search?lastName=
    @GetMapping("/search")
    public Page<PatientResponseDTO> searchByLastName(@RequestParam String lastName,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return this.patientService.findByLastName(lastName, page, size).map(mapper::toPatientDTO);
    }

    // POST /api/patients
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public PatientResponseDTO create(@RequestBody @Validated PatientCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toPatientDTO(this.patientService.savePatient(payload));
    }

    //PUT /api/patients/{patientId}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public PatientResponseDTO update(@PathVariable UUID id,
                                     @RequestBody @Validated PatientUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toPatientDTO(this.patientService.findByIdAndUpdate(id, payload));
    }

    // DELETE /api/patients/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public void delete(@PathVariable UUID id) {
        this.patientService.findByIdAndDelete(id);
    }

    //PATCH CLOUDINARY
    @PatchMapping("/{id}/photo")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public PatientResponseDTO uploadPhoto(@PathVariable UUID id,
                                          @RequestParam("file") MultipartFile file) {
        return mapper.toPatientDTO(this.patientService.uploadPhoto(id, file));
    }

    //GLOBAL SEARCH
    @GetMapping("/global-search")
    public Page<PatientResponseDTO> globalSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int size
    ) {
        return patientService.globalSearch(q, size)
                .map(mapper::toPatientDTO);
    }

}
