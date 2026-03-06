package naderdeghaili.capstoneproject.controllers;


import naderdeghaili.capstoneproject.entities.TreatedTooth;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.TreatedToothCreateDTO;
import naderdeghaili.capstoneproject.payloads.TreatedToothUpdateDTO;
import naderdeghaili.capstoneproject.services.TreatedToothService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/treated-teeth")
@PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST','HYGIENIST')")
public class TreatedToothController {

    private final TreatedToothService treatedToothService;

    public TreatedToothController(TreatedToothService treatedToothService) {
        this.treatedToothService = treatedToothService;
    }

    @GetMapping("/treatment/{treatmentId}")
    public Page<TreatedTooth> getByTreatment(@PathVariable UUID treatmentId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return treatedToothService.findByTreatment(treatmentId, page, size);
    }

    @GetMapping("/patient/{patientId}")
    public Page<TreatedTooth> getByPatient(@PathVariable UUID patientId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return treatedToothService.findByPatient(patientId, page, size);
    }

    @GetMapping("/tooth/{toothCode}")
    public Page<TreatedTooth> getByToothCode(@PathVariable Integer toothCode,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return treatedToothService.findByToothCode(toothCode, page, size);
    }

    @GetMapping("/tooth/{toothCode}/patient/{patientId}")
    public Page<TreatedTooth> getByToothCodeAndPatient(@PathVariable Integer toothCode,
                                                       @PathVariable UUID patientId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return treatedToothService.findByToothCodeAndPatient(toothCode, patientId, page, size);
    }

    @GetMapping("/{toothId}")
    public TreatedTooth getById(@PathVariable UUID toothId) {
        return treatedToothService.findById(toothId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreatedTooth create(@RequestBody @Validated TreatedToothCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return treatedToothService.saveTreatedTooth(payload);
    }

    @PutMapping("/{id}")
    public TreatedTooth update(@PathVariable UUID id,
                               @RequestBody @Validated TreatedToothUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return treatedToothService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        treatedToothService.findByIdAndDelete(id);
    }

}
