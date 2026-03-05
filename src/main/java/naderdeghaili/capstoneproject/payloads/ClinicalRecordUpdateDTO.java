package naderdeghaili.capstoneproject.payloads;

public record ClinicalRecordUpdateDTO(
        String anamnesis,
        String allergies,
        String medications,
        Boolean signedConsent,
        String notes
) {
}
