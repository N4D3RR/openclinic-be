package naderdeghaili.capstoneproject.jobs;


import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Appointment;
import naderdeghaili.capstoneproject.entities.AppointmentStatus;
import naderdeghaili.capstoneproject.repositories.AppointmentRepository;
import naderdeghaili.capstoneproject.services.MailgunService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class AppointmentReminderJob {

    private final AppointmentRepository appointmentRepository;
    private final MailgunService mailgunService;

    public AppointmentReminderJob(AppointmentRepository appointmentRepository,
                                  MailgunService mailgunService) {
        this.appointmentRepository = appointmentRepository;
        this.mailgunService = mailgunService;
    }

    @Scheduled(cron = "0 0 18 * * *")
    //every day at 18:00 (seconds, mimutes, hour, day, month, day of the week)
    public void sendReminders() {

        LocalDateTime tomorrowStart = LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay();
        LocalDateTime tomorrowEnd = tomorrowStart.plusDays(1);

        List<Appointment> appointments = appointmentRepository
                .findByDateTimeBetweenAndStatus(tomorrowStart, tomorrowEnd, AppointmentStatus.CONFIRMED);

        log.info("Sending reminders for " + appointments.size() + " appointments tomorrow");

        for (Appointment appointment : appointments) {
            mailgunService.sendAppointmentReminder(appointment);
        }
    }
}

