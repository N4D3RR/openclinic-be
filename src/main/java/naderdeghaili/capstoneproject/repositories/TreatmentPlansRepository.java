package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.TreatmentPlan;
import naderdeghaili.capstoneproject.entities.TreatmentPlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TreatmentPlansRepository extends JpaRepository<TreatmentPlan, UUID> {
    Optional<TreatmentPlan> findByQuote_Id(UUID id);

    Page<TreatmentPlan> findByStatus(TreatmentPlanStatus status, Pageable pageable);

    Page<TreatmentPlan> findByQuote_Patient_Id(UUID id, Pageable pageable);


}
