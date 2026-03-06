package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.Appointment;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.AppointmentCreateDTO;
import naderdeghaili.capstoneproject.payloads.AppointmentUpdateDTO;
import naderdeghaili.capstoneproject.services.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;


    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    //GET ALL /api/appointments
    @GetMapping
    public Page<Appointment> getAll(@AuthenticationPrincipal User currentUser,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return this.appointmentService.getAll(currentUser, page, size);
    }

    //GET BY ID /api/appointments/{appointmentId}
    //faccio controllo ruolo e ADMIN/SECRETARY vedono tutto, DENTIST solo i propri
    @GetMapping("/{appointmentId}")
    public Appointment getById(@PathVariable UUID appointmentId, @AuthenticationPrincipal User currentUser) {
        return this.appointmentService.findById(appointmentId, currentUser);

    }

    //GET BY PATIENT /api/appointments/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<Appointment> getByPatient(@PathVariable UUID patientId,
                                          @AuthenticationPrincipal User currentUser,
                                          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return this.appointmentService.findByPatient(patientId, currentUser, page, size);
    }

    //GET BY USER /api/appointments/user/{userId}
    @GetMapping("/user/{userId}")
    public Page<Appointment> getByUser(@PathVariable UUID userId,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {

        return this.appointmentService.findByUser(userId, page, size);
    }

    //GET BY DATE RANGE /api/appointments/date-range}
    @GetMapping("/date-range/")
    public Page<Appointment> getByPatient(@AuthenticationPrincipal User currentUser,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {

        return this.appointmentService.findByDateRange(currentUser, from, to, page, size);
    }

    //POST /api/appointments/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment create(@AuthenticationPrincipal User currentUser,
                              @RequestBody @Validated AppointmentCreateDTO payload,
                              BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList());

        return this.appointmentService.saveAppointment(payload, currentUser);
    }

    //PUT /api/appointments/{appointmentId}
    @PutMapping("/{appointmentId}")
    public Appointment update(@PathVariable UUID appointmentId,
                              @AuthenticationPrincipal User currentUser,
                              @RequestBody @Validated AppointmentUpdateDTO payload,
                              BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return this.appointmentService.findByIdAndUpdate(appointmentId, payload, currentUser);
    }

    //DELETE api/appointments/{appointmentId}
    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID appointmentId,
                       @AuthenticationPrincipal User currentUser
    ) {
        this.appointmentService.findByIdAndDelete(appointmentId, currentUser);
    }
    
}
