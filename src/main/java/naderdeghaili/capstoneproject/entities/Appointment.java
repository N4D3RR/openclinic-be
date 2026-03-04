package naderdeghaili.capstoneproject.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    private List<Treatment> treatments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "treatment_plan_id", nullable = true)
    private TreatmentPlan treatmentPlan;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String notes;


    private LocalDateTime createdAt;

    //constructors
    public Appointment() {
    }

    public Appointment(Patient patient, User user, TreatmentPlan treatmentPlan, LocalDateTime dateTime, Integer duration, String notes) {
        this.patient = patient;
        this.user = user;
        this.treatmentPlan = treatmentPlan;
        this.dateTime = dateTime;
        this.duration = duration;
        this.status = AppointmentStatus.CONFIRMED;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public void addTreatment(Treatment treatment) {
        this.treatments.add(treatment);
        treatment.setAppointment(this);
    }

    //getter e setter
    public UUID getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

    public TreatmentPlan getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(TreatmentPlan treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    //toString
    @Override
    public String toString() {
        return "Appointment: " +
                "id: " + id +
                " | patient: " + patient +
                " | dentist: " + user +
                " | dateTime: " + dateTime +
                " | duration: " + duration +
                " | status: " + status +
                " | notes: " + notes +
                " | createdAt: " + createdAt;
    }
}
