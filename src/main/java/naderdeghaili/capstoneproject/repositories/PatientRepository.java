package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {


    Optional<Patient> findByFiscalCode(String fiscalCode);

    Optional<Patient> findByEmail(String email);

    Page<Patient> findByLastNameContainsIgnoreCase(String lastName, Pageable pageable);

    boolean existsByFiscalCode(String fiscalCode);

    boolean existsByEmail(String email);

    @Query("""
                SELECT p FROM Patient p WHERE
                LOWER(p.lastName)   LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(p.firstName)  LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(p.fiscalCode) LIKE LOWER(CONCAT('%', :q, '%')) OR
                p.phone             LIKE CONCAT('%', :q, '%')
            """)
    Page<Patient> globalSearch(@Param("q") String q, Pageable pageable);


}
