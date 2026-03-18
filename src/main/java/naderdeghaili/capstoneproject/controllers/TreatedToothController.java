package naderdeghaili.capstoneproject.controllers;


import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.create.TreatedToothCreateDTO;
import naderdeghaili.capstoneproject.payloads.responses.TreatedToothResponseDTO;
import naderdeghaili.capstoneproject.payloads.update.TreatedToothUpdateDTO;
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
    private final DTOMapper mapper;

    public TreatedToothController(TreatedToothService treatedToothService, DTOMapper mapper) {
        this.treatedToothService = treatedToothService;
        this.mapper = mapper;
    }

    //GET BY TREATMENT - api/treated-tooth
    @GetMapping("/treatment/{treatmentId}")
    public Page<TreatedToothResponseDTO> getByTreatment(@PathVariable UUID treatmentId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return this.treatedToothService.findByTreatment(treatmentId, page, size).map(mapper::toTreatedToothDTO);
    }

    @GetMapping("/patient/{patientId}")
    public Page<TreatedToothResponseDTO> getByPatient(@PathVariable UUID patientId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return this.treatedToothService.findByPatient(patientId, page, size).map(mapper::toTreatedToothDTO);
    }

    @GetMapping("/tooth/{toothCode}")
    public Page<TreatedToothResponseDTO> getByToothCode(@PathVariable Integer toothCode,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return this.treatedToothService.findByToothCode(toothCode, page, size).map(mapper::toTreatedToothDTO);
    }

    @GetMapping("/tooth/{toothCode}/patient/{patientId}")
    public Page<TreatedToothResponseDTO> getByToothCodeAndPatient(@PathVariable Integer toothCode,
                                                                  @PathVariable UUID patientId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        return this.treatedToothService.findByToothCodeAndPatient(toothCode, patientId, page, size).map(mapper::toTreatedToothDTO);
    }

    @GetMapping("/{toothId}")
    public TreatedToothResponseDTO getById(@PathVariable UUID toothId) {
        return mapper.toTreatedToothDTO(this.treatedToothService.findById(toothId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreatedToothResponseDTO create(@RequestBody @Validated TreatedToothCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toTreatedToothDTO(this.treatedToothService.saveTreatedTooth(payload));
    }

    @PutMapping("/{id}")
    public TreatedToothResponseDTO update(@PathVariable UUID id,
                                          @RequestBody @Validated TreatedToothUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toTreatedToothDTO(this.treatedToothService.findByIdAndUpdate(id, payload));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        this.treatedToothService.findByIdAndDelete(id);
    }

}
