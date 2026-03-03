package naderdeghaili.capstoneproject.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "clinical_records")
public class ClinicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String anamnesis;
    @Column(columnDefinition = "TEXT")
    private String allergies;
    @Column(columnDefinition = "TEXT")
    private String medications;

    private Boolean signedConsent;
    private String notes;

    //TODO: eventuale entity Document separata
    private String documents;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;


    public ClinicalRecord() {
    }

    public ClinicalRecord(String anamnesis, String allergies, String medications, String notes, String documents, Patient patient) {
        this.anamnesis = anamnesis;
        this.allergies = allergies;
        this.medications = medications;
        this.signedConsent = false;
        this.notes = notes;
        this.documents = documents;
        this.patient = patient;
    }

    public UUID getId() {
        return id;
    }

    public String getAnamnesis() {
        return anamnesis;
    }

    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public Boolean getSignedConsent() {
        return signedConsent;
    }

    public void setSignedConsent(Boolean signedConsent) {
        this.signedConsent = signedConsent;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
