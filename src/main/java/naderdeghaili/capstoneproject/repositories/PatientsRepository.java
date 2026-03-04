package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientsRepository extends JpaRepository<Patient, UUID> {


    Optional<Patient> findByFiscalCode(String fiscalCode);

    Optional<Patient> findByEmail(String email);

    Page<Patient> findByLastNameContainsIgnoreCase(String lastName, Pageable pageable);

    boolean existsByFiscalCode(String fiscalCode);

    boolean existsByEmail(String email);


}
