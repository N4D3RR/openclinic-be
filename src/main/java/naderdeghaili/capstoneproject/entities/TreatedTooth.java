package naderdeghaili.capstoneproject.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "treated_teeth")
public class TreatedTooth {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer toothCode;

    @ManyToOne
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @Enumerated(EnumType.STRING)
    private ToothSurface surface;

    public TreatedTooth() {
    }

    public TreatedTooth(Integer toothCode, Treatment treatment, ToothSurface surface) {
        this.toothCode = toothCode;
        this.treatment = treatment;
        this.surface = surface;
    }

    public UUID getId() {
        return id;
    }

    public Integer getToothCode() {
        return toothCode;
    }

    public void setToothCode(Integer toothCode) {
        this.toothCode = toothCode;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public ToothSurface getSurface() {
        return surface;
    }

    public void setSurface(ToothSurface surface) {
        this.surface = surface;
    }

    @Override
    public String toString() {
        return "TreatedTooth: " +
                "id: " + id +
                " | toothCode: " + toothCode +
                " | treatment: " + treatment +
                " | surface: " + surface;
    }
}
