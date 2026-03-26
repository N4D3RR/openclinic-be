package naderdeghaili.capstoneproject.services;

import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Appointment;
import naderdeghaili.capstoneproject.entities.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class MailgunService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private final String FROM;
    private final String domain;
    private final String apiKey;

    public MailgunService(@Value("${mailgun.domain}") String domain,
                          @Value("${mailgun.apiKey}") String apiKey) {
        this.domain = domain;
        this.apiKey = apiKey;
        this.FROM = "OpenClinic <mailgun@" + domain + ">";
    }

    public void sendAppointmentConfirmation(Appointment appointment) {
        Patient patient = appointment.getPatient();
        if (!Boolean.TRUE.equals(patient.isEmailConsent())) return;

        String subject = "Conferma appuntamento - OpenClinic";
        String body = "Gentile " + patient.getFirstName() + " " + patient.getLastName() + ",\n\n"
                + "il suo appuntamento è confermato per il giorno "
                + appointment.getDateTime().format(DATE_FMT)
                + " alle ore " + appointment.getDateTime().format(TIME_FMT)
                + " (durata: " + appointment.getDuration() + " minuti).\n\n"
                + "Per disdire o modificare, contatti lo studio.\n\n"
                + "OpenClinic";

        sendEmail(patient.getEmail(), subject, body);
    }

    public void sendAppointmentReminder(Appointment appointment) {
        Patient patient = appointment.getPatient();
        if (!Boolean.TRUE.equals(patient.isEmailConsent())) return;

        String subject = "Promemoria appuntamento domani - OpenClinic";
        String body = "Gentile " + patient.getFirstName() + " " + patient.getLastName() + ",\n\n"
                + "le ricordiamo il suo appuntamento domani "
                + appointment.getDateTime().format(DATE_FMT)
                + " alle ore " + appointment.getDateTime().format(TIME_FMT)
                + ".\n\n"
                + "In caso di impedimento, la preghiamo di avvisarci.\n\n"
                + "OpenClinic";

        sendEmail(patient.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            Unirest.post("https://api.mailgun.net/v3/" + domain + "/messages")
                    .basicAuth("api", apiKey)
                    .queryString("from", this.FROM)
                    .queryString("to", to)
                    .queryString("subject", subject)
                    .queryString("text", body)
                    .asJson();
            log.info("Email sent to " + to + ": " + subject);
        } catch (Exception e) {
            log.error("Failed to send email to " + to, e);
        }
    }
}