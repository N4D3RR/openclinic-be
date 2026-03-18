package naderdeghaili.capstoneproject.mappers;

import naderdeghaili.capstoneproject.entities.*;
import naderdeghaili.capstoneproject.payloads.responses.*;
import org.springframework.stereotype.Component;

/*
 * Per non esporre direttamente le entities, creo un component che contiene metodi per copiare i dati dall'entity in un oggetto da mandare all'utente.
 * risolvo loop nei JSON
 * più sicuro, rendo il DB "indipendente" dall'API
 *
 * */
@Component
public class DTOMapper {


    public UserResponseDTO toUserDTO(User u) {
        if (u == null) return null;
        return new UserResponseDTO(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getRole(),
                u.getCreatedAt()
        );
    }

    public PatientResponseDTO toPatientDTO(Patient p) {
        if (p == null) return null;
        return new PatientResponseDTO(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getBirthDate(),
                p.getFiscalCode(),
                p.getEmail(),
                p.getPhone(),
                p.getAddress(),
                p.getPhotoUrl(),
                p.isEmailConsent(),
                p.getCreatedAt()
        );
    }

    public ProcedureResponseDTO toProcedureDTO(Procedure p) {
        if (p == null) return null;
        return new ProcedureResponseDTO(
                p.getId(),
                p.getCode(),
                p.getName(),
                p.getDescription(),
                p.getDurationInMinutes(),
                p.getPrice(),
                p.getCreatedAt()
        );
    }

    public DocumentResponseDTO toDocumentDTO(Document d) {
        if (d == null) return null;
        return new DocumentResponseDTO(
                d.getId(),
                d.getFileName(),
                d.getFileUrl(),
                d.getType(),
                d.getNotes(),
                d.getUploadedAt(),
                d.getClinicalRecord() != null ? d.getClinicalRecord().getId() : null
        );
    }

    public TreatedToothResponseDTO toTreatedToothDTO(TreatedTooth t) {
        if (t == null) return null;
        return new TreatedToothResponseDTO(
                t.getId(),
                t.getToothCode(),
                t.getSurface(),
                t.getTreatment() != null ? t.getTreatment().getId() : null
        );
    }

    // ─── ENTITÀ CON RELAZIONI (rompe i cicli con ID) ──────────────

    public QuoteItemResponseDTO toQuoteItemDTO(QuoteItem item) {
        if (item == null) return null;
        return new QuoteItemResponseDTO(
                item.getId(),
                item.getToothNumber(),
                item.getQuotedPrice(),

                toProcedureDTO(item.getProcedure()),
                item.getQuote() != null ? item.getQuote().getId() : null

        );
    }

    public AppointmentResponseDTO toAppointmentDTO(Appointment a) {
        if (a == null) return null;
        return new AppointmentResponseDTO(
                a.getId(),
                a.getDateTime(),
                a.getDuration(),
                a.getStatus(),
                a.getNotes(),
                a.getCreatedAt(),

                toPatientDTO(a.getPatient()),
                toUserDTO(a.getUser()),
                a.getTreatmentPlan() != null ? a.getTreatmentPlan().getId() : null
        );
    }

    public TreatmentResponseDTO toTreatmentDTO(Treatment t) {
        if (t == null) return null;
        return new TreatmentResponseDTO(
                t.getId(),
                t.getDate(),
                t.getCost(),
                t.getNotes(),
                t.getImageUrl(),

                toProcedureDTO(t.getProcedure()),
                t.getTreatedToothList().stream().map(this::toTreatedToothDTO).toList(),
                t.getAppointment() != null ? t.getAppointment().getId() : null
        );
    }

    public QuoteResponseDTO toQuoteDTO(Quote q) {
        if (q == null) return null;
        return new QuoteResponseDTO(
                q.getId(),
                q.getStatus(),
                q.getNotes(),
                q.getCreatedAt(),

                toPatientDTO(q.getPatient()),
                toUserDTO(q.getDentist()),
                q.getItems().stream().map(this::toQuoteItemDTO).toList(),


                q.getTreatmentPlan() != null ? q.getTreatmentPlan().getId() : null
        );
    }

    public ClinicalRecordResponseDTO toClinicalRecordDTO(ClinicalRecord r) {
        if (r == null) return null;
        return new ClinicalRecordResponseDTO(
                r.getId(),
                r.getAnamnesis(),
                r.getAllergies(),
                r.getMedications(),
                r.getSignedConsent(),
                r.getNotes(),
                r.getCreatedAt(),
                r.getUpdatedAt(),

                toPatientDTO(r.getPatient()),
                r.getDocuments().stream().map(this::toDocumentDTO).toList()
        );
    }

    public TreatmentPlanResponseDTO toTreatmentPlanDTO(TreatmentPlan plan) {
        if (plan == null) return null;
        return new TreatmentPlanResponseDTO(
                plan.getId(),
                plan.getStatus(),
                plan.getStartDate(),
                plan.getExpectedEndDate(),
                plan.getClinicalNotes(),
                plan.getTotalAmount(),
                plan.getCreatedAt(),
                plan.getUpdatedAt(),

                toQuoteDTO(plan.getQuote()),
                plan.getAppointments().stream().map(this::toAppointmentDTO).toList()
        );
    }

    public PaymentResponseDTO toPaymentDTO(Payment pay) {
        if (pay == null) return null;
        return new PaymentResponseDTO(
                pay.getId(),
                pay.getAmount(),
                pay.getPaymentDate(),
                pay.getMethod(),
                pay.getStatus(),
                pay.getNotes(),
                pay.getCreatedAt(),

                toPatientDTO(pay.getPatient()),
                pay.getAppointment() != null ? pay.getAppointment().getId() : null
        );
    }
}
