package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.*;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.AppointmentCreateDTO;
import naderdeghaili.capstoneproject.payloads.AppointmentUpdateDTO;
import naderdeghaili.capstoneproject.repositories.AppointmentRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final UserService userService;
    private final TreatmentPlanService treatmentPlanService;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientService patientService, UserService userService, @Lazy TreatmentPlanService treatmentPlanService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.userService = userService;
        this.treatmentPlanService = treatmentPlanService;
    }

    // GET ALL
    public Page<Appointment> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return appointmentRepository.findAll(pageable);
    }

    // GET BY ID
    public Appointment findById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment with id " + appointmentId + " not found"));
    }

    // GET BY PATIENT
    public Page<Appointment> findByPatient(UUID patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return appointmentRepository.findByPatient_Id(patientId, pageable);
    }

    // GET BY USER
    public Page<Appointment> findByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return appointmentRepository.findByUser_Id(userId, pageable);
    }

    // GET BY DATE RANGE
    public Page<Appointment> findByDateRange(LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return appointmentRepository.findByDateTimeBetween(start, end, pageable);
    }

    // SAVE
    public Appointment saveAppointment(AppointmentCreateDTO payload) {
        Patient patient = patientService.findById(payload.patientId());
        User user = userService.findByID(payload.userId());

        if (user.getRole() != UserType.DENTIST && user.getRole() != UserType.HYGIENIST)
            throw new IllegalArgumentException("User must be a DENTIST or HYGIENIST to be assigned to an appointment");

        TreatmentPlan treatmentPlan = null;
        if (payload.treatmentPlanId() != null)
            treatmentPlan = treatmentPlanService.findById(payload.treatmentPlanId());

        Appointment appointment = new Appointment(
                patient,
                user,
                treatmentPlan,
                payload.dateTime(),
                payload.duration(),
                payload.notes()
        );

        if (treatmentPlan != null)
            treatmentPlan.addAppointment(appointment);

        log.info("Appointment for patient " + patient.getId() + " saved successfully");
        return appointmentRepository.save(appointment);
    }

    // UPDATE
    public Appointment findByIdAndUpdate(UUID id, AppointmentUpdateDTO payload) {
        Appointment found = this.findById(id);

        if (payload.dateTime() != null) found.setDateTime(payload.dateTime());
        if (payload.duration() != null) found.setDuration(payload.duration());
        if (payload.status() != null) found.setStatus(payload.status());
        if (payload.notes() != null) found.setNotes(payload.notes());

        log.info("Appointment with id " + id + " updated successfully");
        return appointmentRepository.save(found);
    }

    // UPDATE STATUS
    public Appointment updateStatus(UUID appointmentId, AppointmentStatus status) {
        Appointment found = this.findById(appointmentId);
        found.setStatus(status);

        log.info("Appointment with id " + appointmentId + " status updated to " + status);
        return appointmentRepository.save(found);
    }

    // DELETE
    public void findByIdAndDelete(UUID id) {
        Appointment found = this.findById(id);

        appointmentRepository.delete(found);
        log.info("Appointment with id " + id + " deleted successfully");
    }


}
