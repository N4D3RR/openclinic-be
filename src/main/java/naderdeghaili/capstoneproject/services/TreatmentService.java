package naderdeghaili.capstoneproject.services;


import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Appointment;
import naderdeghaili.capstoneproject.entities.Patient;
import naderdeghaili.capstoneproject.entities.Procedure;
import naderdeghaili.capstoneproject.entities.Treatment;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.create.TreatmentCreateDTO;
import naderdeghaili.capstoneproject.payloads.update.TreatmentUpdateDTO;
import naderdeghaili.capstoneproject.repositories.TreatmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final AppointmentService appointmentService;
    private final ProcedureService procedureService;
    private final PatientService patientService;


    public TreatmentService(TreatmentRepository treatmentRepository, AppointmentService appointmentService, ProcedureService procedureService, PatientService patientService) {
        this.treatmentRepository = treatmentRepository;
        this.appointmentService = appointmentService;
        this.procedureService = procedureService;
        this.patientService = patientService;
    }

    //GET ALL
    public Page<Treatment> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatmentRepository.findAll(pageable);
    }

    //GET BY ID
    public Treatment findById(UUID treatmentId) {
        return treatmentRepository.findById(treatmentId).orElseThrow(() -> new NotFoundException("Treatment with id " + treatmentId + " not found"));

    }

    //GET BY APPOINTMENT
    public Page<Treatment> findByAppointment(UUID apppointmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatmentRepository.findByAppointment_Id(apppointmentId, pageable);
    }

    //GET BY PATIENT

    public Page<Treatment> findByPatient(UUID patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatmentRepository.findByPatient_Id(patientId, pageable);
    }

    //GET BY PROCEDURE
    public Page<Treatment> findByProcedure(UUID procedureId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return treatmentRepository.findByProcedure_Id(procedureId, pageable);
    }

    //SAVE
    public Treatment saveTreatment(TreatmentCreateDTO payload) {

        Patient patient = patientService.findById(payload.patientId());

        Appointment appointment = null;
        if (payload.appointmentId() != null) {

            appointment = appointmentService.findById(payload.appointmentId());
            patient = appointment.getPatient();

        }

        Procedure procedure = procedureService.findById(payload.procedureId());

        Treatment treatment = new Treatment(
                appointment,
                patient,
                procedure,
                payload.cost(),
                payload.notes(),
                payload.imageUrl(),
                payload.date()
        );
        if (appointment != null) {
            appointment.addTreatment(treatment);
        }

        log.info("Treatment saved " + (appointment != null ? " for appointment " + appointment.getId() : "without appointment"));
        return treatmentRepository.save(treatment);
    }

    //UPDATE
    public Treatment findByIdAndUpdate(UUID treatmentId, TreatmentUpdateDTO payload) {
        Treatment found = this.findById(treatmentId);

        if (payload.cost() != null) found.setCost(payload.cost());
        if (payload.notes() != null) found.setNotes(payload.notes());
        if (payload.imageUrl() != null) found.setImageUrl(payload.imageUrl());
        if (payload.date() != null) found.setDate(payload.date());

        log.info("Treatment with id " + treatmentId + " updated successfully");
        return treatmentRepository.save(found);
    }

    //DELETE
    public void findByIdAndDelete(UUID treatmentId) {
        Treatment found = this.findById(treatmentId);
        treatmentRepository.delete(found);
        log.info("Treatment with id " + treatmentId + " deleted successfully");
    }
}
