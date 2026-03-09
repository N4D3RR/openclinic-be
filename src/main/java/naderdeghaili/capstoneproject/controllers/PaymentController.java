package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.PaymentStatus;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.mappers.DTOMapper;
import naderdeghaili.capstoneproject.payloads.PaymentCreateDTO;
import naderdeghaili.capstoneproject.payloads.PaymentResponseDTO;
import naderdeghaili.capstoneproject.payloads.PaymentUpdateDTO;
import naderdeghaili.capstoneproject.services.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
public class PaymentController {

    private final PaymentService paymentService;
    private final DTOMapper mapper;

    public PaymentController(PaymentService paymentService, DTOMapper mapper) {
        this.paymentService = paymentService;
        this.mapper = mapper;
    }

    //GET ALL - /api/payments
    @GetMapping
    public Page<PaymentResponseDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return this.paymentService.getAll(page, size).map(mapper::toPaymentDTO);
    }

    //GET BY ID - /api/payments/{paymentId}
    @GetMapping("/{id}")
    public PaymentResponseDTO getById(@PathVariable UUID id) {
        return mapper.toPaymentDTO(this.paymentService.findById(id));
    }

    //GET BY PATIENT - /api/payments/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<PaymentResponseDTO> getByPatient(@PathVariable UUID patientId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return this.paymentService.findByPatient(patientId, page, size).map(mapper::toPaymentDTO);
    }

    //GET BY APPOINTMENT - /api/payments/apopintment/{appointmentId}
    @GetMapping("/appointment/{appointmentId}")
    public Page<PaymentResponseDTO> getByAppointment(@PathVariable UUID appointmentId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return this.paymentService.findByAppointment(appointmentId, page, size).map(mapper::toPaymentDTO);
    }

    //GET BY STATUS - /api/payments/status
    @GetMapping("/status")
    public Page<PaymentResponseDTO> getByStatus(@RequestParam PaymentStatus status,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return this.paymentService.findByStatus(status, page, size).map(mapper::toPaymentDTO);
    }

    //GET PENDING BY PATIENT - /api/payments/patient/{patientId}/pending
    @GetMapping("/patient/{patientId}/pending")
    public List<PaymentResponseDTO> getPendingByPatient(@PathVariable UUID patientId) {
        return this.paymentService.findPendingByPatient(patientId).stream().map(mapper::toPaymentDTO).toList();
    }

    //POST /api/payments
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDTO create(@RequestBody @Validated PaymentCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toPaymentDTO(this.paymentService.savePayment(payload));
    }

    //PUT /api/payments/{paymentId}
    @PutMapping("/{paymentId}")
    public PaymentResponseDTO update(@PathVariable UUID paymentId,
                                     @RequestBody @Validated PaymentUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return mapper.toPaymentDTO(this.paymentService.findByIdAndUpdate(paymentId, payload));
    }

    //DELETE /api/payments/{paymentId}
    @DeleteMapping("/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable UUID paymentId) {
        this.paymentService.findByIdAndDelete(paymentId);
    }
}
