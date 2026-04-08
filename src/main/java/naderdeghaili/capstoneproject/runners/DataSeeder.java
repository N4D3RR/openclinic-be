package naderdeghaili.capstoneproject.runners;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.*;
import naderdeghaili.capstoneproject.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@Order(2) //runs after AdminRunner
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ProcedureRepository procedureRepository;
    private final AppointmentRepository appointmentRepository;
    private final QuoteRepository quoteRepository;
    private final PaymentRepository paymentRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository userRepository, PatientRepository patientRepository,
                      ProcedureRepository procedureRepository, AppointmentRepository appointmentRepository,
                      QuoteRepository quoteRepository, PaymentRepository paymentRepository,
                      ClinicalRecordRepository clinicalRecordRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.procedureRepository = procedureRepository;
        this.appointmentRepository = appointmentRepository;
        this.quoteRepository = quoteRepository;
        this.paymentRepository = paymentRepository;
        this.clinicalRecordRepository = clinicalRecordRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (patientRepository.count() > 0) {
            log.info("Database already seeded, skipping");
            return;
        }

        log.info("Seeding demo data...");

        //USERS
        User dentist = userRepository.save(new User("Marco", "Bianchi", "marco.bianchi@openclinic.it", encoder.encode("Password1!"), UserType.DENTIST));
        User hygienist = userRepository.save(new User("Sara", "Conti", "sara.conti@openclinic.it", encoder.encode("Password1!"), UserType.HYGIENIST));
        User secretary = userRepository.save(new User("Giulia", "Moretti", "giulia.moretti@openclinic.it", encoder.encode("Password1!"), UserType.SECRETARY));

        //PROCEDURES
        Procedure visita = procedureRepository.save(new Procedure("VIS01", "First visit", "Complete oral examination with diagnosis", 30, new BigDecimal("50.00")));
        Procedure detartrasi = procedureRepository.save(new Procedure("IGI01", "Dental cleaning", "Professional scaling and polishing", 45, new BigDecimal("80.00")));
        Procedure otturazione = procedureRepository.save(new Procedure("CON01", "Composite filling", "Direct composite restoration", 30, new BigDecimal("120.00")));
        Procedure devitalizzazione = procedureRepository.save(new Procedure("END01", "Root canal treatment", "Endodontic therapy with obturation", 60, new BigDecimal("350.00")));
        Procedure estrazione = procedureRepository.save(new Procedure("CHI01", "Simple extraction", "Simple tooth extraction", 30, new BigDecimal("100.00")));
        Procedure corona = procedureRepository.save(new Procedure("PRO01", "Porcelain crown", "Metal-ceramic or full-ceramic crown", 45, new BigDecimal("600.00")));
        Procedure panoramica = procedureRepository.save(new Procedure("RAD01", "Panoramic X-ray", "Orthopantomography", 15, new BigDecimal("40.00")));
        Procedure sbiancamento = procedureRepository.save(new Procedure("EST01", "Teeth whitening", "Professional in-office whitening", 60, new BigDecimal("250.00")));
        Procedure impianto = procedureRepository.save(new Procedure("IMP01", "Dental implant", "Titanium implant placement", 90, new BigDecimal("1200.00")));
        Procedure faccetta = procedureRepository.save(new Procedure("PRO02", "Porcelain veneer", "Minimal preparation veneer", 45, new BigDecimal("500.00")));

        //PATIENTS
        Patient p1 = patientRepository.save(new Patient("Luca", "Rossi", LocalDate.of(1985, 3, 15), "RSSLCU85C15H501X", "luca.rossi@email.it", "3331234567", "Via Roma 12, Padova"));
        Patient p2 = patientRepository.save(new Patient("Anna", "Ferrari", LocalDate.of(1992, 7, 22), "FRRNNA92L62F205Y", "anna.ferrari@email.it", "3409876543", "Via Garibaldi 8, Milano"));
        Patient p3 = patientRepository.save(new Patient("Giuseppe", "Esposito", LocalDate.of(1978, 11, 5), "SPSGPP78S05F839Z", "giuseppe.esposito@email.it", "3281122334", "Corso Vittorio 45, Padova"));
        Patient p4 = patientRepository.save(new Patient("Maria", "Romano", LocalDate.of(1995, 1, 30), "RMNMRA95A70A662W", "maria.romano@email.it", "3475566778", "Via Dante 3, Vicenza"));
        Patient p5 = patientRepository.save(new Patient("Francesco", "Colombo", LocalDate.of(1988, 9, 12), "CLMFNC88P12L736V", "francesco.colombo@email.it", "3661234987", "Piazza Duomo 1, Treviso"));
        Patient p6 = patientRepository.save(new Patient("Elena", "Ricci", LocalDate.of(2001, 5, 8), "RCCLNE01E48G224U", "elena.ricci@email.it", "3209988776", "Via Manzoni 22, Padova"));
        Patient p7 = patientRepository.save(new Patient("Alessandro", "Marino", LocalDate.of(1970, 12, 18), "MRNLSS70T18C351T", "alessandro.marino@email.it", "3551234567", "Via Verdi 7, Venezia"));
        Patient p8 = patientRepository.save(new Patient("Sofia", "Greco", LocalDate.of(1999, 4, 3), "GRCSFO99D43D969S", "sofia.greco@email.it", "3387654321", "Via Carducci 15, Padova"));

        //CLINICAL RECORDS
        clinicalRecordRepository.save(new ClinicalRecord("No significant medical history", "None", "None", "Regular check-ups recommended", p1));
        clinicalRecordRepository.save(new ClinicalRecord("Hypertension under treatment", "Penicillin", "Amlodipine 5mg", "Monitor BP before procedures", p2));
        clinicalRecordRepository.save(new ClinicalRecord("Type 2 diabetes", "Latex", "Metformin 500mg", "Healing time may be longer", p3));
        clinicalRecordRepository.save(new ClinicalRecord("No significant medical history", "None", "None", null, p4));
        clinicalRecordRepository.save(new ClinicalRecord("Anxiety disorder", "None", "Alprazolam as needed", "May need sedation for longer procedures", p5));
        clinicalRecordRepository.save(new ClinicalRecord("No significant medical history", "None", "None", "Orthodontic treatment completed 2023", p6));

        //APPOINTMENTS (past, today, future)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today9 = now.toLocalDate().atTime(9, 0);
        LocalDateTime today10 = now.toLocalDate().atTime(10, 0);
        LocalDateTime today11 = now.toLocalDate().atTime(11, 30);
        LocalDateTime today14 = now.toLocalDate().atTime(14, 0);
        LocalDateTime today15 = now.toLocalDate().atTime(15, 30);

        // Past appointments (completed)
        Appointment a1 = new Appointment(p1, dentist, null, now.minusDays(30).withHour(9).withMinute(0), 30, "First visit - complete examination");
        a1.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(a1);

        Appointment a2 = new Appointment(p2, dentist, null, now.minusDays(25).withHour(10).withMinute(0), 45, "Dental cleaning");
        a2.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(a2);

        Appointment a3 = new Appointment(p3, dentist, null, now.minusDays(20).withHour(11).withMinute(0), 30, "Filling on tooth 36");
        a3.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(a3);

        Appointment a4 = new Appointment(p1, hygienist, null, now.minusDays(15).withHour(9).withMinute(0), 45, "Professional cleaning");
        a4.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(a4);

        Appointment a5 = new Appointment(p4, dentist, null, now.minusDays(10).withHour(14).withMinute(0), 30, "First visit");
        a5.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(a5);

        Appointment a6 = new Appointment(p5, dentist, null, now.minusDays(5).withHour(10).withMinute(0), 60, "Root canal 46");
        a6.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(a6);

        // No-show
        Appointment a7 = new Appointment(p7, dentist, null, now.minusDays(3).withHour(15).withMinute(0), 30, "Scheduled visit - patient did not show");
        a7.setStatus(AppointmentStatus.NO_SHOW);
        appointmentRepository.save(a7);

        // Today's appointments
        appointmentRepository.save(new Appointment(p2, dentist, null, today9, 30, "Check-up and filling evaluation"));
        appointmentRepository.save(new Appointment(p6, hygienist, null, today10, 45, "Professional cleaning"));
        appointmentRepository.save(new Appointment(p3, dentist, null, today11, 60, "Crown preparation tooth 46"));
        appointmentRepository.save(new Appointment(p8, dentist, null, today14, 30, "First visit"));
        appointmentRepository.save(new Appointment(p1, hygienist, null, today15, 45, "Periodontal maintenance"));

        // Future appointments
        appointmentRepository.save(new Appointment(p3, dentist, null, now.plusDays(7).withHour(9).withMinute(0), 45, "Crown cementation"));
        appointmentRepository.save(new Appointment(p5, dentist, null, now.plusDays(7).withHour(11).withMinute(0), 30, "Post-endo check"));
        appointmentRepository.save(new Appointment(p4, dentist, null, now.plusDays(14).withHour(10).withMinute(0), 60, "Whitening session"));
        appointmentRepository.save(new Appointment(p7, dentist, null, now.plusDays(14).withHour(14).withMinute(0), 30, "Rescheduled first visit"));

        //QUOTES
        Quote q1 = new Quote(QuoteStatus.ACCEPTED, "Crown restoration after root canal", p3, dentist);
        q1.addItem(new QuoteItem(46, new BigDecimal("600.00"), q1, corona));
        q1.addItem(new QuoteItem(46, new BigDecimal("40.00"), q1, panoramica));
        quoteRepository.save(q1);

        Quote q2 = new Quote(QuoteStatus.SENT, "Aesthetic treatment proposal", p4, dentist);
        q2.addItem(new QuoteItem(11, new BigDecimal("500.00"), q2, faccetta));
        q2.addItem(new QuoteItem(21, new BigDecimal("500.00"), q2, faccetta));
        q2.addItem(new QuoteItem(0, new BigDecimal("250.00"), q2, sbiancamento));
        quoteRepository.save(q2);

        Quote q3 = new Quote(QuoteStatus.DRAFT, "Implant evaluation", p7, dentist);
        q3.addItem(new QuoteItem(36, new BigDecimal("1200.00"), q3, impianto));
        q3.addItem(new QuoteItem(36, new BigDecimal("600.00"), q3, corona));
        quoteRepository.save(q3);

        Quote q4 = new Quote(QuoteStatus.REJECTED, "Conservative treatment", p5, dentist);
        q4.addItem(new QuoteItem(15, new BigDecimal("120.00"), q4, otturazione));
        q4.addItem(new QuoteItem(25, new BigDecimal("120.00"), q4, otturazione));
        quoteRepository.save(q4);

        //PAYMENTS (spread across months for dashboard charts)
        paymentRepository.save(new Payment(new BigDecimal("50.00"), LocalDate.now().minusMonths(5).withDayOfMonth(10), PaymentMethod.CARD, PaymentStatus.PAID, null, p1, a1));
        paymentRepository.save(new Payment(new BigDecimal("80.00"), LocalDate.now().minusMonths(4).withDayOfMonth(15), PaymentMethod.CASH, PaymentStatus.PAID, null, p2, a2));
        paymentRepository.save(new Payment(new BigDecimal("120.00"), LocalDate.now().minusMonths(3).withDayOfMonth(5), PaymentMethod.CARD, PaymentStatus.PAID, null, p3, a3));
        paymentRepository.save(new Payment(new BigDecimal("80.00"), LocalDate.now().minusMonths(3).withDayOfMonth(20), PaymentMethod.BANK_TRANSFER, PaymentStatus.PAID, null, p1, a4));
        paymentRepository.save(new Payment(new BigDecimal("50.00"), LocalDate.now().minusMonths(2).withDayOfMonth(8), PaymentMethod.CASH, PaymentStatus.PAID, null, p4, a5));
        paymentRepository.save(new Payment(new BigDecimal("350.00"), LocalDate.now().minusMonths(1).withDayOfMonth(12), PaymentMethod.CARD, PaymentStatus.PAID, "Root canal treatment", p5, a6));
        paymentRepository.save(new Payment(new BigDecimal("200.00"), LocalDate.now().minusDays(5), PaymentMethod.CARD, PaymentStatus.PAID, null, p3, null));
        paymentRepository.save(new Payment(new BigDecimal("400.00"), LocalDate.now().minusDays(2), PaymentMethod.BANK_TRANSFER, PaymentStatus.PENDING, "Remaining balance for crown", p3, null));
        paymentRepository.save(new Payment(new BigDecimal("250.00"), LocalDate.now(), PaymentMethod.CARD, PaymentStatus.PENDING, "Whitening deposit", p4, null));

        log.info("Demo data seeded: 3 users, 10 procedures, 8 patients, 6 clinical records, 16 appointments, 4 quotes, 9 payments");
    }
}
