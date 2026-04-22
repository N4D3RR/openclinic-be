package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Procedure;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.create.ProcedureCreateDTO;
import naderdeghaili.capstoneproject.payloads.update.ProcedureUpdateDTO;
import naderdeghaili.capstoneproject.repositories.ProcedureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class ProcedureService {

    private final ProcedureRepository procedureRepository;


    public ProcedureService(ProcedureRepository procedureRepository) {
        this.procedureRepository = procedureRepository;
    }

    //GET ALL
    @Transactional(readOnly = true)
    public Page<Procedure> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return procedureRepository.findAll(pageable);
    }

    //GET BY ID
    @Transactional(readOnly = true)
    public Procedure findById(UUID procedureId) {
        return procedureRepository.findById(procedureId)
                .orElseThrow(() -> new NotFoundException("Procedure with id " + procedureId + " not found"));
    }

    //SEARCH BY NAME
    @Transactional(readOnly = true)
    public Page<Procedure> findByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return procedureRepository.findByNameContainsIgnoreCase(name, pageable);
    }

    //SAVE
    @Transactional
    public Procedure save(ProcedureCreateDTO payload) {
        if (procedureRepository.existsByCode(payload.code()))
            throw new IllegalArgumentException("Code " + payload.code() + " already in use");

        Procedure procedure = new Procedure(
                payload.code(),
                payload.name(),
                payload.description(),
                payload.durationInMinutes(),
                payload.price()
        );

        log.info("Procedure " + payload.code() + " saved successfully");
        return procedureRepository.save(procedure);
    }

    //UPDATE
    @Transactional
    public Procedure findByIdAndUpdate(UUID procedureId, ProcedureUpdateDTO payload) {
        Procedure found = this.findById(procedureId);

        if (payload.name() != null) found.setName(payload.name());
        if (payload.description() != null) found.setDescription(payload.description());
        if (payload.durationInMinutes() != null) found.setDurationInMinutes(payload.durationInMinutes());
        if (payload.price() != null) found.setPrice(payload.price());

        log.info("Procedure with id " + procedureId + " updated successfully");
        return procedureRepository.save(found);
    }

    //DELETE
    @Transactional
    public void findByIdAndDelete(UUID procedureId) {
        Procedure found = this.findById(procedureId);
        procedureRepository.delete(found);
        log.info("Procedure with id " + procedureId + " deleted successfully");
    }

}
