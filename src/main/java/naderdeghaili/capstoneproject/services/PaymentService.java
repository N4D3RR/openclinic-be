package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Appointment;
import naderdeghaili.capstoneproject.entities.Patient;
import naderdeghaili.capstoneproject.entities.Payment;
import naderdeghaili.capstoneproject.entities.PaymentStatus;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.create.PaymentCreateDTO;
import naderdeghaili.capstoneproject.payloads.responses.MonthlyRevenueResponseDTO;
import naderdeghaili.capstoneproject.payloads.update.PaymentUpdateDTO;
import naderdeghaili.capstoneproject.repositories.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PaymentService(PaymentRepository paymentRepository, PatientService patientService,
                          AppointmentService appointmentService) {
        this.paymentRepository = paymentRepository;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    //GET ALL
    public Page<Payment> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findAll(pageable);
    }

    //GET BY ID
    public Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment with id " + paymentId + " not found"));
    }

    //GET BY PATIENT
    public Page<Payment> findByPatient(UUID patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findByPatient_Id(patientId, pageable);
    }

    //GET BY APPOINTMENT
    public Page<Payment> findByAppointment(UUID appointmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findByAppointment_Id(appointmentId, pageable);
    }

    //GET BY STATUS
    public Page<Payment> findByStatus(PaymentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findByStatus(status, pageable);
    }

    //GET PENDING PAYMENTS BY PATIENT (per calcolo debiti)
    public List<Payment> findPendingByPatient(UUID patientId) {
        return paymentRepository.findByPatient_IdAndStatus(patientId, PaymentStatus.PENDING);
    }

    //SAVE
    public Payment savePayment(PaymentCreateDTO payload) {
        Patient patient = patientService.findById(payload.patientId());

        Appointment appointment = null;
        if (payload.appointmentId() != null)
            appointment = appointmentService.findById(payload.appointmentId());

        Payment payment = new Payment(
                payload.amount(),
                payload.paymentDate(),
                payload.method(),
                payload.status(),
                payload.notes(),
                patient,
                appointment
        );

        log.info("Payment of " + payload.amount() + " for patient " + patient.getId() + " saved successfully");
        return paymentRepository.save(payment);
    }

    //UPDATE
    public Payment findByIdAndUpdate(UUID id, PaymentUpdateDTO payload) {
        Payment found = this.findById(id);

        if (payload.amount() != null) found.setAmount(payload.amount());
        if (payload.paymentDate() != null) found.setPaymentDate(payload.paymentDate());
        if (payload.method() != null) found.setMethod(payload.method());
        if (payload.status() != null) found.setStatus(payload.status());
        if (payload.notes() != null) found.setNotes(payload.notes());

        log.info("Payment with id " + id + " updated successfully");
        return paymentRepository.save(found);
    }

    //DELETE
    public void findByIdAndDelete(UUID id) {
        Payment found = this.findById(id);
        paymentRepository.delete(found);
        log.info("Payment with id " + id + " deleted successfully");
    }

    //GET BY PERIOD
    public Page<Payment> findByDateRange(LocalDate from, LocalDate to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findByPaymentDateBetween(from, to, pageable);
    }

    //GET BY PERIOD AND STATUS
    public Page<Payment> findByDateRangeAndStatus(LocalDate from, LocalDate to, PaymentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findByPaymentDateBetweenAndStatus(from, to, status, pageable);
    }

    //KPI
    public Map<String, Object> getKpi() {
        LocalDate now = LocalDate.now();
        LocalDate prev = now.minusMonths(1);

        List<MonthlyRevenueResponseDTO> monthly = paymentRepository.monthlyRevenue()
                .stream()
                .map(row -> new MonthlyRevenueResponseDTO(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        (BigDecimal) row[2]
                ))
                .toList();

        Map<String, Object> kpi = new HashMap<>();
        kpi.put("totalPaid", paymentRepository.sumByStatus(PaymentStatus.PAID));
        kpi.put("totalPending", paymentRepository.sumByStatus(PaymentStatus.PENDING));
        kpi.put("totalPartial", paymentRepository.sumByStatus(PaymentStatus.PARTIAL));
        kpi.put("paidCount", paymentRepository.countByStatus(PaymentStatus.PAID));
        kpi.put("pendingCount", paymentRepository.countByStatus(PaymentStatus.PENDING));
        kpi.put("partialCount", paymentRepository.countByStatus(PaymentStatus.PARTIAL));
        kpi.put("currentMonth", paymentRepository.revenueByYearMonth(PaymentStatus.PAID, now.getYear(), now.getMonthValue()));
        kpi.put("previousMonth", paymentRepository.revenueByYearMonth(PaymentStatus.PAID, prev.getYear(), prev.getMonthValue()));
        kpi.put("monthlyRevenue", monthly);

        return kpi;
    }
}