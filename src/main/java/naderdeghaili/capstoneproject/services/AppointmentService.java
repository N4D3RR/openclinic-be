package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.*;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.exceptions.UnauthorizedException;
import naderdeghaili.capstoneproject.payloads.create.AppointmentCreateDTO;
import naderdeghaili.capstoneproject.payloads.update.AppointmentUpdateDTO;
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

    // GET ALL - ADMIN e SECRETARY vedono tutto, DENTIST solo i propri
    public Page<Appointment> getAll(User currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (isAdminOrSecretary(currentUser)) {
            return appointmentRepository.findAll(pageable);
        }
        return appointmentRepository.findByUser_Id(currentUser.getId(), pageable);
    }

    // GET BY ID - ADMIN e SECRETARY vedono tutto, DENTIST solo i propri
    public Appointment findById(UUID appointmentId, User currentUser) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment with id " + appointmentId + " not found"));
        checkOwnership(appointment, currentUser);
        return appointment;
    }

    //GET BY ID
    public Appointment findById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment with id " + appointmentId + " not found"));
    }

    // GET BY PATIENT - ADMIN e SECRETARY vedono tutto, DENTIST solo i propri
    public Page<Appointment> findByPatient(UUID patientId, User currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (isAdminOrSecretary(currentUser)) {
            return appointmentRepository.findByPatient_Id(patientId, pageable);
        }
        return appointmentRepository.findByPatient_IdAndUser_Id(patientId, currentUser.getId(), pageable);
    }

    // GET BY USER - APPUNTAMENTI SINGOLO DENTISTA
    public Page<Appointment> findByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return appointmentRepository.findByUser_Id(userId, pageable);
    }

    // GET BY DATE RANGE - ADMIN e SECRETARY vedono tutto, DENTIST solo i propri
    public Page<Appointment> findByDateRange(User currentUser, LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (isAdminOrSecretary(currentUser)) {
            return appointmentRepository.findByDateTimeBetween(start, end, pageable);
        }
        return appointmentRepository.findByUser_IdAndDateTimeBetween(currentUser.getId(), start, end, pageable);
    }

    // SAVE - ADMIN e SECRETARY salvano tutto, DENTIST/HYGIENIST solo i propri appuntamenti
    public Appointment saveAppointment(AppointmentCreateDTO payload, User currentUser) {
        Patient patient = patientService.findById(payload.patientId());
        User user;

        if (isAdminOrSecretary(currentUser)) {
            user = userService.findByID(payload.userId());
        } else {
            user = currentUser;
        }

        if (user.getRole() != UserType.DENTIST && user.getRole() != UserType.HYGIENIST)
            throw new IllegalArgumentException("User must be a DENTIST or HYGIENIST to be assigned to an appointment");

        TreatmentPlan treatmentPlan = null;
        if (payload.treatmentPlanId() != null)
            treatmentPlan = treatmentPlanService.findById(payload.treatmentPlanId());

        LocalDateTime end = payload.dateTime().plusMinutes(payload.duration());
        if (appointmentRepository.existsByUser_IdAndDateTimeBetween(user.getId(), payload.dateTime(), end))
            throw new IllegalArgumentException("Dentist already has an appointment in this time slot");

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

    // UPDATE - ADMIN e SECRETARY modificano tutto, DENTIST solo i propri
    public Appointment findByIdAndUpdate(UUID id, AppointmentUpdateDTO payload, User currentUser) {
        Appointment found = this.findById(id, currentUser);

        if (payload.dateTime() != null) found.setDateTime(payload.dateTime());
        if (payload.duration() != null) found.setDuration(payload.duration());
        if (payload.status() != null) found.setStatus(payload.status());
        if (payload.notes() != null) found.setNotes(payload.notes());

        Appointment saved = appointmentRepository.save(found);

        //verifica e segna come completato
        if (payload.status() == AppointmentStatus.COMPLETED) {
            checkAndCompletePlan(saved);
        }

        log.info("Appointment with id " + id + " updated successfully");
        return saved;
    }

    // UPDATE STATUS
    public Appointment updateStatus(UUID appointmentId, AppointmentStatus status, User currentUser) {
        Appointment found = this.findById(appointmentId, currentUser);
        found.setStatus(status);

        log.info("Appointment with id " + appointmentId + " status updated to " + status);
        return appointmentRepository.save(found);
    }

    // DELETE - ADMIN/SECRETARY eliminano tutto, DENTIST/HYGIENIST solo i propri
    public void findByIdAndDelete(UUID id, User currentUser) {

        Appointment found = this.findById(id, currentUser);

        appointmentRepository.delete(found);
        log.info("Appointment with id " + id + " deleted successfully");
    }


    //HELPER
    private boolean isAdminOrSecretary(User user) {
        return user.getRole() == UserType.ADMIN || user.getRole() == UserType.SECRETARY;
    }

    //verifica se appuntamento/paziente è dell'user non admin che accede
    private void checkOwnership(Appointment appointment, User currentUser) {

        if (!isAdminOrSecretary(currentUser) && !appointment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only access your own appointments");
        }
    }

    //verifica stato appuntamenti per completare piano di cura
    //quando tutti gli appuntamenti sono COMPLETED treatmentPlan viene completato automaticamente
    private void checkAndCompletePlan(Appointment appointment) {
        TreatmentPlan plan = appointment.getTreatmentPlan();
        if (plan == null) return;
        if (plan.getStatus() == TreatmentPlanStatus.COMPLETED) return;

        boolean allCompleted = plan.getAppointments().stream()
                .allMatch(a -> a.getStatus() == AppointmentStatus.COMPLETED);

        if (allCompleted && !plan.getAppointments().isEmpty()) {
            plan.setStatus(TreatmentPlanStatus.COMPLETED);
            treatmentPlanService.save(plan);
            log.info("TreatmentPlan " + plan.getId() + " auto-completed");
        }
    }

}
