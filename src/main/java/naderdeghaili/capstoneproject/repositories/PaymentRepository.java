package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Payment;
import naderdeghaili.capstoneproject.entities.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findByPatient_Id(UUID id, Pageable pageable);

    Page<Payment> findByAppointment_Id(UUID id, Pageable pageable);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    //debiti paziente
    List<Payment> findByPatient_IdAndStatus(UUID id, PaymentStatus status);


}
