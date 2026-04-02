package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.create.ProcedureCreateDTO;
import naderdeghaili.capstoneproject.payloads.responses.ProcedureResponseDTO;
import naderdeghaili.capstoneproject.payloads.update.ProcedureUpdateDTO;
import naderdeghaili.capstoneproject.services.ProcedureService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/procedures")
public class ProcedureController {

    private final ProcedureService procedureService;
    private final DTOMapper mapper;

    public ProcedureController(ProcedureService procedureService, DTOMapper mapper) {
        this.procedureService = procedureService;
        this.mapper = mapper;
    }

    //GET ALL - api/procedures
    @GetMapping
    public Page<ProcedureResponseDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return this.procedureService.getAll(page, size).map(mapper::toProcedureDTO);
    }

    //GET BY ID - api/procedures/{id}
    @GetMapping("/{id}")
    public ProcedureResponseDTO getById(@PathVariable UUID id) {
        return mapper.toProcedureDTO(this.procedureService.findById(id));
    }

    //GET BY NAME - api/procedures/search
    @GetMapping("/search")
    public Page<ProcedureResponseDTO> searchByName(@RequestParam String name,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return this.procedureService.findByName(name, page, size).map(mapper::toProcedureDTO);
    }

    //POST - api/procedures
    //ADMIN ONLY
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ProcedureResponseDTO create(@RequestBody @Validated ProcedureCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toProcedureDTO(this.procedureService.save(payload));
    }

    //PUT - api/procedures/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ProcedureResponseDTO update(@PathVariable UUID id,
                                       @RequestBody @Validated ProcedureUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toProcedureDTO(this.procedureService.findByIdAndUpdate(id, payload));
    }

    //DELETE - api/procedures/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable UUID id) {
        this.procedureService.findByIdAndDelete(id);
    }
}