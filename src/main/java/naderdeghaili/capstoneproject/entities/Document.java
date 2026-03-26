package naderdeghaili.capstoneproject.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    private String notes;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "clinical_record_id", nullable = false)
    private ClinicalRecord clinicalRecord;


    public Document() {
    }

    public Document(String fileName, String fileUrl, DocumentType type, String notes, ClinicalRecord clinicalRecord) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.type = type;
        this.notes = notes;
        this.uploadedAt = LocalDateTime.now();
        this.clinicalRecord = clinicalRecord;
    }

    public UUID getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public ClinicalRecord getClinicalRecord() {
        return clinicalRecord;
    }

    public void setClinicalRecord(ClinicalRecord clinicalRecord) {
        this.clinicalRecord = clinicalRecord;
    }

    @Override
    public String toString() {
        return "Document:" +
                "id: " + id +
                " | fileName:" + fileName +
                " | fileUrl:" + fileUrl +
                " | type: " + type +
                " | notes:" + notes +
                " | uploadedAt: " + uploadedAt +
                " | clinicalRecord: " + (clinicalRecord != null ? clinicalRecord.getId() : null)
                ;
    }
}