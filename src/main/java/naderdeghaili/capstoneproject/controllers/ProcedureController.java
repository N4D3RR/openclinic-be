package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.Procedure;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.ProcedureCreateDTO;
import naderdeghaili.capstoneproject.payloads.ProcedureUpdateDTO;
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

    public ProcedureController(ProcedureService procedureService) {
        this.procedureService = procedureService;
    }

    // GET — tutti gli utenti autenticati
    @GetMapping
    public Page<Procedure> getAll(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return procedureService.getAll(page, size);
    }

    @GetMapping("/{id}")
    public Procedure getById(@PathVariable UUID id) {
        return procedureService.findById(id);
    }

    @GetMapping("/search")
    public Page<Procedure> searchByName(@RequestParam String name,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return procedureService.findByName(name, page, size);
    }

    // POST/PUT/DELETE — solo ADMIN
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Procedure create(@RequestBody @Validated ProcedureCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return procedureService.save(payload);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Procedure update(@PathVariable UUID id,
                            @RequestBody @Validated ProcedureUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return procedureService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable UUID id) {
        procedureService.findByIdAndDelete(id);
    }
}