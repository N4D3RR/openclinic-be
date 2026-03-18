package naderdeghaili.capstoneproject.controllers;


import naderdeghaili.capstoneproject.entities.DocumentType;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.responses.DocumentResponseDTO;
import naderdeghaili.capstoneproject.payloads.update.DocumentUpdateDTO;
import naderdeghaili.capstoneproject.services.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@PreAuthorize("hasAnyAuthority('ADMIN', 'DENTIST', 'HYGIENIST')")
public class DocumentController {

    private final DocumentService documentService;
    private final DTOMapper mapper;

    public DocumentController(DocumentService documentService, DTOMapper mapper) {
        this.documentService = documentService;
        this.mapper = mapper;
    }

    //GET ALL - /api/documents
    @GetMapping
    public Page<DocumentResponseDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return this.documentService.getAll(page, size).map(mapper::toDocumentDTO);
    }

    //GET BY ID - /api/documents/{id}
    @GetMapping("/{documentId}")
    public DocumentResponseDTO getById(@PathVariable UUID documentId) {
        return mapper.toDocumentDTO(this.documentService.findById(documentId));
    }

    //GET BY CLINICAL RECORD - /api/documents
    @GetMapping("/clinical-record/{clinicalRecordId}")
    public Page<DocumentResponseDTO> getByClinicalRecord(@PathVariable UUID clinicalRecordId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return this.documentService.findByClinicalRecord(clinicalRecordId, page, size).map(mapper::toDocumentDTO);
    }

    //GET BY PATIENT - /api/documents/patient/{id}
    @GetMapping("/patient/{patientId}")
    public Page<DocumentResponseDTO> getByPatient(@PathVariable UUID patientId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return this.documentService.findByPatient(patientId, page, size).map(mapper::toDocumentDTO);
    }

    //GET BY CLINICAL RECORD AND TYPE - /api/documents/clinical-record/{clinicalRecord}/type
    @GetMapping("/clinical-record/{clinicalRecordId}/type")
    public Page<DocumentResponseDTO> getByClinicalRecordAndType(@PathVariable UUID clinicalRecordId,
                                                                @RequestParam DocumentType type,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return this.documentService.findByClinicalRecordAndType(clinicalRecordId, type, page, size).map(mapper::toDocumentDTO);
    }

    //POST - /api/documents
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)//per swagger
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponseDTO create(@RequestParam("file") MultipartFile file,
                                      @RequestParam("clinicalRecordId") UUID clinicalRecordId,
                                      @RequestParam("type") DocumentType type,
                                      @RequestParam(value = "notes", required = false) String notes) {
        if (file.isEmpty())
            throw new IllegalArgumentException("File is required");

        return mapper.toDocumentDTO(this.documentService.saveDocument(file, clinicalRecordId, type, notes));
    }

    //PUT - /api/documents
    @PutMapping("/{documentId}")
    public DocumentResponseDTO update(@PathVariable UUID documentId,
                                      @RequestBody @Validated DocumentUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toDocumentDTO(this.documentService.findByIdAndUpdate(documentId, payload));
    }

    //DELETE - /api/documents/{id}
    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID documentId) {
        this.documentService.findByIdAndDelete(documentId);
    }
}