package naderdeghaili.capstoneproject.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "treatment_plans")
public class TreatmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @OneToMany(mappedBy = "treatmentPlan", cascade = CascadeType.ALL)
    private List<Appointment> appointments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatmentPlanStatus status;

    private LocalDate startDate;
    private LocalDate expectedEndDate;

    @Column(columnDefinition = "TEXT")
    private String clinicalNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Double totalAmount;

    public TreatmentPlan() {
    }

    public TreatmentPlan(Quote quote, TreatmentPlanStatus status, LocalDate expectedEndDate, String clinicalNotes) {
        this.quote = quote;
        this.status = status;
        this.expectedEndDate = expectedEndDate;
        this.clinicalNotes = clinicalNotes;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addAppointment(Appointment appointment) {
        if (this.appointments.isEmpty()) {
            this.startDate = appointment.getDateTime().toLocalDate();
        }
        this.appointments.add(appointment);
        appointment.setTreatmentPlan(this);
    }

    public UUID getId() {
        return id;
    }


    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public TreatmentPlanStatus getStatus() {
        return status;
    }

    public void setStatus(TreatmentPlanStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(LocalDate expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TreatmentPlan: " +
                "id: " + id +
                " | quote: " + quote +
                " | appointments: " + appointments +
                " | status: " + status +
                " | startDate: " + startDate +
                " | expectedEndDate: " + expectedEndDate +
                " | clinicalNotes: '" + clinicalNotes +
                " | totalAmount: " + totalAmount +
                " | createdAt: " + createdAt
                ;
    }
}