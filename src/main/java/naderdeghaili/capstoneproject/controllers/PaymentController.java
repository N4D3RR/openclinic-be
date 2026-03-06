package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.entities.Payment;
import naderdeghaili.capstoneproject.entities.PaymentStatus;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.payloads.PaymentCreateDTO;
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

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    //GET ALL - /api/payments
    @GetMapping
    public Page<Payment> getAll(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return paymentService.getAll(page, size);
    }

    //GET BY ID - /api/payments/{paymentId}
    @GetMapping("/{id}")
    public Payment getById(@PathVariable UUID id) {
        return paymentService.findById(id);
    }

    //GET BY PATIENT - /api/payments/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public Page<Payment> getByPatient(@PathVariable UUID patientId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return paymentService.findByPatient(patientId, page, size);
    }

    //GET BY APPOINTMENT - /api/payments/apopintment/{appointmentId}
    @GetMapping("/appointment/{appointmentId}")
    public Page<Payment> getByAppointment(@PathVariable UUID appointmentId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return paymentService.findByAppointment(appointmentId, page, size);
    }

    //GET BY STATUS - /api/payments/status
    @GetMapping("/status")
    public Page<Payment> getByStatus(@RequestParam PaymentStatus status,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return paymentService.findByStatus(status, page, size);
    }

    //GET PENDING BY PATIENT - /api/payments/patient/{patientId}/pending
    @GetMapping("/patient/{patientId}/pending")
    public List<Payment> getPendingByPatient(@PathVariable UUID patientId) {
        return paymentService.findPendingByPatient(patientId);
    }

    //POST /api/payments
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment create(@RequestBody @Validated PaymentCreateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return paymentService.savePayment(payload);
    }

    //PUT /api/payments/{paymentId}
    @PutMapping("/{paymentId}")
    public Payment update(@PathVariable UUID paymentId,
                          @RequestBody @Validated PaymentUpdateDTO payload, BindingResult validation) {
        if (validation.hasErrors())
            throw new ValidationException(validation.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).toList());

        return paymentService.findByIdAndUpdate(paymentId, payload);
    }

    //DELETE /api/payments/{paymentId}
    @DeleteMapping("/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID paymentId) {
        paymentService.findByIdAndDelete(paymentId);
    }
}
