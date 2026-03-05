package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.ClinicalRecord;
import naderdeghaili.capstoneproject.entities.Document;
import naderdeghaili.capstoneproject.entities.DocumentType;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.DocumentCreateDTO;
import naderdeghaili.capstoneproject.payloads.DocumentUpdateDTO;
import naderdeghaili.capstoneproject.repositories.DocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ClinicalRecordService clinicalRecordService;

    public DocumentService(DocumentRepository documentRepository, ClinicalRecordService clinicalRecordService) {
        this.documentRepository = documentRepository;
        this.clinicalRecordService = clinicalRecordService;
    }

    //GET ALL
    public Page<Document> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentRepository.findAll(pageable);
    }

    //GET BY ID
    public Document findById(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document with id " + documentId + " not found"));
    }

    //GET BY CLINICAL RECORD
    public Page<Document> findByClinicalRecord(UUID clinicalRecordId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentRepository.findByClinicalRecord_Id(clinicalRecordId, pageable);
    }

    //GET BY PATIENT
    public Page<Document> findByPatient(UUID patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentRepository.findByClinicalRecord_Patient_Id(patientId, pageable);
    }

    //GET BY CLINICAL RECORD AND TYPE
    public Page<Document> findByClinicalRecordAndType(UUID clinicalRecordId, DocumentType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentRepository.findByClinicalRecord_IdAndType(clinicalRecordId, type, pageable);
    }

    //SAVE
    public Document saveDocument(DocumentCreateDTO payload) {
        ClinicalRecord clinicalRecord = clinicalRecordService.findById(payload.clinicalRecordId());

        Document document = new Document(
                payload.fileName(),
                payload.fileUrl(),
                payload.type(),
                payload.notes(),
                clinicalRecord
        );

        clinicalRecord.addDocument(document);

        log.info("Document '" + payload.fileName() + "' added to clinical record " + clinicalRecord.getId());
        return documentRepository.save(document);
    }

    //UPDATE
    public Document findByIdAndUpdate(UUID id, DocumentUpdateDTO payload) {
        Document found = this.findById(id);

        if (payload.fileName() != null) found.setFileName(payload.fileName());
        if (payload.fileUrl() != null) found.setFileUrl(payload.fileUrl());
        if (payload.type() != null) found.setType(payload.type());
        if (payload.notes() != null) found.setNotes(payload.notes());

        log.info("Document with id " + id + " updated successfully");
        return documentRepository.save(found);
    }

    //DELETE
    public void findByIdAndDelete(UUID id) {
        Document found = this.findById(id);
        documentRepository.delete(found);
        log.info("Document with id " + id + " deleted successfully");
    }
}
