package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.TreatedTooth;
import naderdeghaili.capstoneproject.entities.Treatment;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.create.TreatedToothCreateDTO;
import naderdeghaili.capstoneproject.payloads.update.TreatedToothUpdateDTO;
import naderdeghaili.capstoneproject.repositories.TreatedToothRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class TreatedToothService {

    private final TreatedToothRepository treatedToothRepository;
    private final TreatmentService treatmentService;

    public TreatedToothService(TreatedToothRepository treatedToothRepository, TreatmentService treatmentService) {
        this.treatedToothRepository = treatedToothRepository;
        this.treatmentService = treatmentService;
    }

    //GET ALL
    @Transactional(readOnly = true)
    public Page<TreatedTooth> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatedToothRepository.findAll(pageable);
    }

    //GET BY ID
    @Transactional(readOnly = true)
    public TreatedTooth findById(UUID treatedToothId) {
        return treatedToothRepository.findById(treatedToothId)
                .orElseThrow(() -> new NotFoundException("TreatedTooth with id " + treatedToothId + " not found"));
    }

    //GET BY TREATMENT
    @Transactional(readOnly = true)
    public Page<TreatedTooth> findByTreatment(UUID treatmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatedToothRepository.findByTreatment_Id(treatmentId, pageable);
    }

    //GET BY PATIENT
    @Transactional(readOnly = true)
    public Page<TreatedTooth> findByPatient(UUID patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatedToothRepository.findByTreatment_Appointment_Patient_Id(patientId, pageable);
    }

    //GET BY TOOTH CODE (storico dente specifico)
    @Transactional(readOnly = true)
    public Page<TreatedTooth> findByToothCode(Integer toothCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatedToothRepository.findByToothCode(toothCode, pageable);
    }

    //GET BY TOOTH CODE AND PATIENT (storico dente specifico di paziente specifico)
    @Transactional(readOnly = true)
    public Page<TreatedTooth> findByToothCodeAndPatient(Integer toothCode, UUID patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatedToothRepository.findByToothCodeAndTreatment_Appointment_Patient_Id(toothCode, patientId, pageable);
    }

    //SAVE
    @Transactional
    public TreatedTooth saveTreatedTooth(TreatedToothCreateDTO payload) {
        Treatment treatment = treatmentService.findById(payload.treatmentId());

        TreatedTooth treatedTooth = new TreatedTooth(
                payload.toothCode(),
                treatment,
                payload.surface()
        );

        treatment.addTreatedTooth(treatedTooth);

        log.info("TreatedTooth (tooth " + payload.toothCode() + ") added to treatment " + treatment.getId());
        return treatedToothRepository.save(treatedTooth);
    }

    // UPDATE
    @Transactional
    public TreatedTooth findByIdAndUpdate(UUID id, TreatedToothUpdateDTO payload) {
        TreatedTooth found = this.findById(id);

        if (payload.toothCode() != null) found.setToothCode(payload.toothCode());
        if (payload.surface() != null) found.setSurface(payload.surface());

        log.info("TreatedTooth with id " + id + " updated successfully");
        return treatedToothRepository.save(found);
    }

    // DELETE
    @Transactional
    public void findByIdAndDelete(UUID id) {
        TreatedTooth found = this.findById(id);
        treatedToothRepository.delete(found);
        log.info("TreatedTooth with id " + id + " deleted successfully");
    }
}
