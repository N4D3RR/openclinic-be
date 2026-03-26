package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.ClinicalRecord;
import naderdeghaili.capstoneproject.entities.Patient;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.create.ClinicalRecordCreateDTO;
import naderdeghaili.capstoneproject.payloads.update.ClinicalRecordUpdateDTO;
import naderdeghaili.capstoneproject.repositories.ClinicalRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ClinicalRecordService {

    private final ClinicalRecordRepository clinicalRecordRepository;
    private final PatientService patientService;


    public ClinicalRecordService(ClinicalRecordRepository clinicalRecordRepository, PatientService patientService) {
        this.clinicalRecordRepository = clinicalRecordRepository;
        this.patientService = patientService;
    }

    //GET ALL
    public Page<ClinicalRecord> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return clinicalRecordRepository.findAll(pageable);
    }

    //GET BY ID
    public ClinicalRecord findById(UUID clinicalRecordId) {
        return clinicalRecordRepository.findById(clinicalRecordId)
                .orElseThrow(() -> new NotFoundException("ClinicalRecord with id " + clinicalRecordId + " not found"));
    }

    //GET BY PATIENT
    public ClinicalRecord findByPatient(UUID patientId) {
        return clinicalRecordRepository.findByPatient_Id(patientId)
                .orElseThrow(() -> new NotFoundException("ClinicalRecord for patient " + patientId + " not found"));
    }

    //SAVE
    public ClinicalRecord saveClinicalRecord(ClinicalRecordCreateDTO payload) {
        Patient patient = patientService.findById(payload.patientId());

        if (clinicalRecordRepository.existsByPatient_Id(patient.getId()))
            throw new IllegalArgumentException("Patient " + patient.getId() + " already has a clinical record");

        ClinicalRecord clinicalRecord = new ClinicalRecord(
                payload.anamnesis(),
                payload.allergies(),
                payload.medications(),
                payload.notes(),
                patient
        );

        log.info("ClinicalRecord for patient " + patient.getId() + " saved successfully");
        return clinicalRecordRepository.save(clinicalRecord);
    }

    //UPDATE
    public ClinicalRecord findByIdAndUpdate(UUID id, ClinicalRecordUpdateDTO payload) {
        ClinicalRecord found = this.findById(id);

        if (payload.anamnesis() != null) found.setAnamnesis(payload.anamnesis());
        if (payload.allergies() != null) found.setAllergies(payload.allergies());
        if (payload.medications() != null) found.setMedications(payload.medications());
        if (payload.signedConsent() != null) found.setSignedConsent(payload.signedConsent());
        if (payload.notes() != null) found.setNotes(payload.notes());

        log.info("ClinicalRecord with id " + id + " updated successfully");
        return clinicalRecordRepository.save(found);
    }

    //DELETE
    public void findByIdAndDelete(UUID id) {
        ClinicalRecord found = this.findById(id);
        clinicalRecordRepository.delete(found);
        log.info("ClinicalRecord with id " + id + " deleted successfully");
    }
}
