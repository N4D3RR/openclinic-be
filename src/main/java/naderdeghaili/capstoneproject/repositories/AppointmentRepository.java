package naderdeghaili.capstoneproject.repositories;

import naderdeghaili.capstoneproject.entities.Appointment;
import naderdeghaili.capstoneproject.entities.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Page<Appointment> findByPatient_Id(UUID id, Pageable pageable);

    Page<Appointment> findByUser_Id(UUID id, Pageable pageable);

    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    Page<Appointment> findByDateTimeBetween(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, Pageable pageable);

    Page<Appointment> findByUser_IdAndDateTimeBetween(UUID id, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, Pageable pageable);

    Page<Appointment> findByTreatmentPlan_Id(UUID id, Pageable pageable);

    Page<Appointment> findByPatient_IdAndUser_Id(UUID id, UUID id1, Pageable pageable);

    boolean existsByUser_IdAndDateTimeBetween(UUID id, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd);

    List<Appointment> findByDateTimeBetweenAndStatus(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, AppointmentStatus status);


}
