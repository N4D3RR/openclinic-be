package naderdeghaili.capstoneproject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "treatments")
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;


    @ManyToOne
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL)
    private List<TreatedTooth> treatedToothList = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal cost;

    private String notes;
    private String imageUrl;

    @Column(nullable = false)
    private LocalDate date;

    public Treatment() {
    }


    public Treatment(Appointment appointment, Patient patient, Procedure procedure, BigDecimal cost, String notes, String imageUrl, LocalDate date) {
        this.appointment = appointment;
        this.patient = patient;
        this.procedure = procedure;
        this.cost = cost;
        this.notes = notes;
        this.imageUrl = imageUrl;
        this.date = date;
    }


    public void addTreatedTooth(TreatedTooth tooth) {
        this.treatedToothList.add(tooth);
        tooth.setTreatment(this);
    }

    public User getUser() {
        return this.appointment != null ? appointment.getUser() : null;
    }

    public UUID getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }


    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<TreatedTooth> getTreatedToothList() {
        return treatedToothList;
    }

    @Override
    public String toString() {
        return "Treatment: " +
                "id: " + id +
                " | appointment: " + (appointment != null ? appointment.getId() : null) +
                " | procedure: " + (procedure != null ? procedure.getName() : null) +
                " | cost: " + cost +
                " | notes: " + notes +
                " | imageUrl: " + imageUrl +
                " | date: " + date;
    }
}
