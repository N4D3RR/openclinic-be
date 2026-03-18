package naderdeghaili.capstoneproject.payloads.update;

public record ClinicalRecordUpdateDTO(
        String anamnesis,
        String allergies,
        String medications,
        Boolean signedConsent,
        String notes
) {
}
