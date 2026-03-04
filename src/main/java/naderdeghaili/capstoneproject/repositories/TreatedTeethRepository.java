package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.TreatedTooth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TreatedTeethRepository extends JpaRepository<TreatedTooth, UUID> {
    Page<TreatedTooth> findByTreatment_Id(UUID id, Pageable pageable);

    //lista denti trattati del paziente
    Page<TreatedTooth> findByTreatment_Appointment_Patient_Id(UUID id, Pageable pageable);

    //storico dente specifico
    Page<TreatedTooth> findByToothCode(Integer toothCode, Pageable pageable);


}
