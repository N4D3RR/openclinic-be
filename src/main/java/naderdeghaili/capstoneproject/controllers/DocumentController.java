package naderdeghaili.capstoneproject.controllers;


import naderdeghaili.capstoneproject.entities.Document;
import naderdeghaili.capstoneproject.entities.DocumentType;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.DocumentCreateDTO;
import naderdeghaili.capstoneproject.payloads.DocumentUpdateDTO;
import naderdeghaili.capstoneproject.services.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST', 'HYGIENIST')")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    //GET ALL - /api/documents
    @GetMapping
    public Page<Document> getAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return this.documentService.getAll(page, size);
    }

    //GET BY ID - /api/documents/{id}
    @GetMapping("/{documentId}")
    public Document getById(@PathVariable UUID documentId) {
        return this.documentService.findById(documentId);
    }

    //GET BY CLINICAL RECORD - /api/documents
    @GetMapping("/clinical-record/{clinicalRecordId}")
    public Page<Document> getByClinicalRecord(@PathVariable UUID clinicalRecordId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return this.documentService.findByClinicalRecord(clinicalRecordId, page, size);
    }

    //GET BY PATIENT - /api/documents/patient/{id}
    @GetMapping("/patient/{patientId}")
    public Page<Document> getByPatient(@PathVariable UUID patientId,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return this.documentService.findByPatient(patientId, page, size);
    }

    //GET BY CLINICAL RECORD AND TYPE - /api/documents/clinical-record/{clinicalRecord}/type
    @GetMapping("/clinical-record/{clinicalRecordId}/type")
    public Page<Document> getByClinicalRecordAndType(@PathVariable UUID clinicalRecordId,
                                                     @RequestParam DocumentType type,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return this.documentService.findByClinicalRecordAndType(clinicalRecordId, type, page, size);
    }

    //POST - /api/documents
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Document create(@RequestBody @Validated DocumentCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return this.documentService.saveDocument(payload);
    }

    //PUT - /api/documents
    @PutMapping("/{documentId}")
    public Document update(@PathVariable UUID documentId,
                           @RequestBody @Validated DocumentUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return this.documentService.findByIdAndUpdate(documentId, payload);
    }

    //DELETE - /api/documents/{id}
    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID documentId) {
        this.documentService.findByIdAndDelete(documentId);
    }
}