package naderdeghaili.capstoneproject.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuoteStatus status;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "dentist_id", nullable = false)
    private User dentist;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private List<QuoteItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "quote", cascade = CascadeType.ALL)
    private TreatmentPlan treatmentPlan;


    public Quote() {
    }


    public Quote(QuoteStatus status, String notes, Patient patient, User dentist, List<QuoteItem> items) {

        this.status = status;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
        this.patient = patient;
        this.dentist = dentist;
        this.items = items;
    }

    public void addItem(QuoteItem item) {
        this.items.add(item);
        item.setQuote(this);
    }

    public UUID getId() {
        return id;
    }

    public QuoteStatus getStatus() {
        return status;
    }

    public void setStatus(QuoteStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public User getDentist() {
        return dentist;
    }

    public void setDentist(User dentist) {
        this.dentist = dentist;
    }

    public List<QuoteItem> getItems() {
        return items;
    }

    public void setItems(List<QuoteItem> items) {
        this.items = items;
    }

    public TreatmentPlan getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(TreatmentPlan treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    @Override
    public String toString() {
        return "Quote: " +
                "id: " + id +
                " | status: " + status +
                " | notes: " + notes +
                " | createdAt: " + createdAt +
                " | patient: " + patient +
                " | dentist: " + dentist +
                " | items: " + items;
    }
}
