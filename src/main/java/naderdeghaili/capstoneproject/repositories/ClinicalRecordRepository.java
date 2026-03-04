package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, UUID> {
    Optional<ClinicalRecord> findByPatient_Id(UUID id);

    boolean existsByPatient_Id(UUID id);


}
