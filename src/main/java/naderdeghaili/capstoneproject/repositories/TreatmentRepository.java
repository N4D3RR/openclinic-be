package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Treatment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {


    Page<Treatment> findByAppointment_Id(UUID id, Pageable pageable);

    //storico trattamenti paziente
    Page<Treatment> findByAppointment_Patient_Id(UUID id, Pageable pageable);

    Page<Treatment> findByProcedure_Id(UUID id, Pageable pageable);

}
