package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Patient;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.PatientCreateDTO;
import naderdeghaili.capstoneproject.payloads.PatientUpdateDTO;
import naderdeghaili.capstoneproject.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final CloudinaryService cloudinaryService;

    public PatientService(PatientRepository patientRepository, CloudinaryService cloudinaryService) {
        this.patientRepository = patientRepository;
        this.cloudinaryService = cloudinaryService;
    }

    //GET ALL
    public Page<Patient> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return patientRepository.findAll(pageable);
    }

    //GET BY ID
    public Patient findById(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient with id " + id + " not found"));
    }

    //SEARCH BY LAST NAME
    public Page<Patient> findByLastName(String lastName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return patientRepository.findByLastNameContainsIgnoreCase(lastName, pageable);
    }

    //SAVE
    public Patient savePatient(PatientCreateDTO payload) {
        if (patientRepository.existsByFiscalCode(payload.fiscalCode()))
            throw new IllegalArgumentException("Fiscal code " + payload.fiscalCode() + " already in use");
        if (patientRepository.existsByEmail(payload.email()))
            throw new IllegalArgumentException("Email " + payload.email() + " already in use");

        Patient patient = new Patient(
                payload.firstName(),
                payload.lastName(),
                payload.birthDate(),
                payload.fiscalCode(),
                payload.email(),
                payload.phone(),
                payload.address()
        );

        log.info("Patient " + payload.fiscalCode() + " saved successfully");
        return patientRepository.save(patient);
    }

    //UPDATE
    public Patient findByIdAndUpdate(UUID id, PatientUpdateDTO payload) {
        Patient found = this.findById(id);

        if (payload.email() != null && !found.getEmail().equals(payload.email())
                && patientRepository.existsByEmail(payload.email()))
            throw new IllegalArgumentException("Email " + payload.email() + " already in use");

        if (payload.fiscalCode() != null && !found.getFiscalCode().equals(payload.fiscalCode())
                && patientRepository.existsByFiscalCode(payload.fiscalCode()))
            throw new IllegalArgumentException("Fiscal code " + payload.fiscalCode() + " already in use");

        if (payload.firstName() != null) found.setFirstName(payload.firstName());
        if (payload.lastName() != null) found.setLastName(payload.lastName());
        if (payload.birthDate() != null) found.setBirthDate(payload.birthDate());
        if (payload.fiscalCode() != null) found.setFiscalCode(payload.fiscalCode());
        if (payload.email() != null) found.setEmail(payload.email());
        if (payload.phone() != null) found.setPhone(payload.phone());
        if (payload.address() != null) found.setAddress(payload.address());

        log.info("Patient with id " + id + " updated successfully");
        return patientRepository.save(found);
    }

    //DELETE
    public void findByIdAndDelete(UUID id) {
        Patient found = this.findById(id);
        patientRepository.delete(found);
        log.info("Patient with id " + id + " deleted successfully");
    }

    //UPLOAD PHOTO
    public Patient uploadPhoto(UUID patientId, MultipartFile file) {

        Patient found = this.findById(patientId);
        String imageUrl = cloudinaryService.upload(file);
        found.setPhotoUrl(imageUrl);

        log.info("Photo uploaded for patient: " + found.getId());
        return patientRepository.save(found);
    }
}
