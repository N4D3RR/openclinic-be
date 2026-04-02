package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Procedure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProcedureRepository extends JpaRepository<Procedure, UUID> {
    Optional<Procedure> findByCode(String code);

    //search procedures
    Page<Procedure> findByNameContainsIgnoreCase(String name, Pageable pageable);

    boolean existsByCode(String code);


}
