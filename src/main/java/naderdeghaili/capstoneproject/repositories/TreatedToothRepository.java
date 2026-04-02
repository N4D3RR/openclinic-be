package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.TreatedTooth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TreatedToothRepository extends JpaRepository<TreatedTooth, UUID> {
    Page<TreatedTooth> findByTreatment_Id(UUID id, Pageable pageable);

    //treted teeth list by patient
    Page<TreatedTooth> findByTreatment_Appointment_Patient_Id(UUID id, Pageable pageable);

    //history for tooth
    Page<TreatedTooth> findByToothCode(Integer toothCode, Pageable pageable);

    Page<TreatedTooth> findByToothCodeAndTreatment_Appointment_Patient_Id(Integer toothCode, UUID patientId, Pageable pageable);


}
