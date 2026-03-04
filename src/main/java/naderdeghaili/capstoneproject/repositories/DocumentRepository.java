package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Document;
import naderdeghaili.capstoneproject.entities.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Page<Document> findByClinicalRecord_Id(UUID id, Pageable pageable);

    Page<Document> findByClinicalRecord_Patient_Id(UUID id, Pageable pageable);

    //filtro tipi documento
    Page<Document> findByClinicalRecord_IdAndType(UUID id, DocumentType type, Pageable pageable);


}
