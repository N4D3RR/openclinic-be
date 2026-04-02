package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.Appointment;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.create.AppointmentCreateDTO;
import naderdeghaili.capstoneproject.payloads.responses.AppointmentResponseDTO;
import naderdeghaili.capstoneproject.payloads.update.AppointmentUpdateDTO;
import naderdeghaili.capstoneproject.services.AppointmentService;
import naderdeghaili.capstoneproject.services.MailgunService;
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
    private final MailgunService mailgunService;
    private final DTOMapper mapper;


    public AppointmentController(AppointmentService appointmentService, MailgunService mailgunService, DTOMapper mapper) {
        this.appointmentService = appointmentService;
        this.mailgunService = mailgunService;
        this.mapper = mapper;
    }

    //
    //GET ALL /api/appointments
    @GetMapping
    public Page<AppointmentResponseDTO> getAll(@AuthenticationPrincipal User currentUser,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        return this.appointmentService.getAll(currentUser, page, size).map(app -> mapper.toAppointmentDTO(app));
    }

    //GET BY ID /api/appointments/{appointmentId}
    //check roles ADMIN/SECRETARY see anything, DENTIST only their own
    @GetMapping("/{appointmentId}")
    public AppointmentResponseDTO getById(@PathVariable UUID appointmentId, @AuthenticationPrincipal User currentUser) {
        return mapper.toAppointmentDTO(this.appointmentService.findById(appointmentId, currentUser));

    }

    //GET BY PATIENT /api/appointments/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<AppointmentResponseDTO> getByPatient(@PathVariable UUID patientId,
                                                     @AuthenticationPrincipal User currentUser,
                                                     @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return this.appointmentService.findByPatient(patientId, currentUser, page, size).map(mapper::toAppointmentDTO);
    }

    //GET BY USER /api/appointments/user/{userId}
    @GetMapping("/user/{userId}")
    public Page<AppointmentResponseDTO> getByUser(@PathVariable UUID userId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {

        return this.appointmentService.findByUser(userId, page, size).map(mapper::toAppointmentDTO);
    }

    //GET BY DATE RANGE /api/appointments/date-range
    @GetMapping("/date-range")
    public Page<AppointmentResponseDTO> getByDateRange(@AuthenticationPrincipal User currentUser,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {

        return this.appointmentService.findByDateRange(currentUser, from, to, page, size).map(mapper::toAppointmentDTO);
    }

    //POST /api/appointments/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDTO create(@AuthenticationPrincipal User currentUser,
                                         @RequestBody @Validated AppointmentCreateDTO payload,
                                         BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList());

        Appointment saved = appointmentService.saveAppointment(payload, currentUser);
        mailgunService.sendAppointmentConfirmation(saved);
        return mapper.toAppointmentDTO(saved);
    }

    //PUT /api/appointments/{appointmentId}
    @PutMapping("/{appointmentId}")
    public AppointmentResponseDTO update(@PathVariable UUID appointmentId,
                                         @AuthenticationPrincipal User currentUser,
                                         @RequestBody @Validated AppointmentUpdateDTO payload,
                                         BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toAppointmentDTO(this.appointmentService.findByIdAndUpdate(appointmentId, payload, currentUser));
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
