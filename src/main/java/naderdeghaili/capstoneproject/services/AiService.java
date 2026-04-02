package naderdeghaili.capstoneproject.services;


import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Appointment;
import naderdeghaili.capstoneproject.repositories.AppointmentRepository;
import naderdeghaili.capstoneproject.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class AiService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    @Value("${openrouter.api.key}")
    private String apiKey;
    @Value("${openrouter.model}")
    private String model;

    public AiService(PatientRepository patientRepository,
                     AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public String chat(List<Map<String, String>> messages) {
        String systemPrompt = buildSystemPrompt();

        //build messages array with system prompt prepended
        StringBuilder messagesJson = new StringBuilder("[");
        messagesJson.append("{\"role\":\"system\",\"content\":")
                .append(toJson(systemPrompt)).append("}");

        for (Map<String, String> msg : messages) {
            messagesJson.append(",{\"role\":")
                    .append(toJson(msg.get("role")))
                    .append(",\"content\":")
                    .append(toJson(msg.get("content")))
                    .append("}");
        }
        messagesJson.append("]");

        String body = "{\"model\":" + toJson(model) +
                ",\"messages\":" + messagesJson + "}";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "http://localhost:5173")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.debug("OpenRouter response: {}", response.body());
            return extractContent(response.body());

        } catch (Exception e) {
            log.error("AI chat error: {}", e.getMessage());
            throw new RuntimeException("Errore nella comunicazione con l'AI", e);
        }
    }

    private String buildSystemPrompt() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        long totalPatients = patientRepository.count();

        //appuntamenti di oggi
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        List<Appointment> todayAppointments = appointmentRepository
                .findByDateTimeBetween(startOfDay, endOfDay);

        StringBuilder apptSummary = new StringBuilder();
        for (Appointment a : todayAppointments) {
            apptSummary.append("- ")
                    .append(a.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .append(" | ")
                    .append(a.getPatient().getFirstName()).append(" ")
                    .append(a.getPatient().getLastName())
                    .append(" | ").append(a.getStatus())
                    .append("\n");
        }

        return """
                Sei un assistente virtuale integrato in OpenClinic, un gestionale per studi odontoiatrici.
                Il tuo compito è aiutare il personale dello studio (dentisti, segretarie, assistenti) a:
                - Navigare e usare l'applicazione
                - Rispondere a domande sui dati dello studio
                - Fornire informazioni odontoiatriche generali
                
                Rispondi in modo molto conciso, massimo 2-3 frasi. Non spiegare il tuo ragionamento.
                Rispondi sempre in italiano, in modo chiaro e conciso.
                Se non sai qualcosa, dillo chiaramente senza inventare.
                
                === DATI AGGIORNATI DELLO STUDIO ===
                Data odierna: %s
                Pazienti registrati: %d
                Appuntamenti oggi (%d):
                %s
                
                === GUIDA ALL'APP ===
                - Dashboard: panoramica KPI, appuntamenti di oggi, pazienti recenti
                - Pazienti: anagrafica, cartella clinica, odontogramma, preventivi, pagamenti
                - Appuntamenti: calendario interattivo, drag & drop per spostare
                - Preventivi: creazione, invio, accettazione (genera piano di cura automatico)
                - Pagamenti: registro incassi, KPI finanziari, download fattura PDF
                - Prestazioni: catalogo procedure con codici e prezzi
                - Utenti: gestione ruoli (Admin, Dentist, Secretary)
                """.formatted(
                today.format(fmt),
                totalPatients,
                todayAppointments.size(),
                apptSummary.length() > 0 ? apptSummary.toString() : "Nessun appuntamento"
        );
    }

    private String extractContent(String json) {
        //gestione errori
        if (json.contains("\"error\"")) {
            if (json.contains("rate-limit") || json.contains("429"))
                return "Il servizio AI è temporaneamente occupato. Riprova tra qualche istante.";
            return "Errore del servizio AI. Riprova più tardi.";
        }
        //parsing manuale — evita dipendenze aggiuntive

        int contentIdx = json.indexOf("\"content\":");
        if (contentIdx == -1) throw new RuntimeException("Risposta AI non valida");
        int valueStart = contentIdx + 10;
        while (valueStart < json.length() && json.charAt(valueStart) == ' ') valueStart++;
        if (contentIdx == -1) throw new RuntimeException("Risposta AI non valida");
        if (json.startsWith("null", valueStart)) throw new RuntimeException("Risposta AI non valida");
        int start = json.indexOf("\"", valueStart) + 1;
        int end = start;
        while (end < json.length()) {
            if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') break;
            end++;
        }

        String content = json.substring(start, end);
        return content.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private String toJson(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }
}
