package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.Patient;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.PatientCreateDTO;
import naderdeghaili.capstoneproject.payloads.PatientUpdateDTO;
import naderdeghaili.capstoneproject.services.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    //GET ALL /api/patients
    @GetMapping
    public Page<Patient> getAll(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return patientService.getAll(page, size);
    }

    //GET BY ID /api/patients/{patientId}
    @GetMapping("/{patientId}")
    public Patient getById(@PathVariable UUID patientId) {
        return patientService.findById(patientId);
    }

    // GET BY LASTNAME /api/patients/search?lastName=
    @GetMapping("/search")
    public Page<Patient> searchByLastName(@RequestParam String lastName,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return patientService.findByLastName(lastName, page, size);
    }

    // POST /api/patients
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@RequestBody @Validated PatientCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return patientService.savePatient(payload);
    }

    //PUT /api/patients/{patientId}
    @PutMapping("/{id}")
    public Patient update(@PathVariable UUID id,
                          @RequestBody @Validated PatientUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return patientService.findByIdAndUpdate(id, payload);
    }

    // DELETE /api/patients/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        patientService.findByIdAndDelete(id);
    }

}
