package naderdeghaili.capstoneproject.services;

import naderdeghaili.capstoneproject.entities.Appointment;
import naderdeghaili.capstoneproject.entities.Patient;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.entities.UserType;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.exceptions.UnauthorizedException;
import naderdeghaili.capstoneproject.payloads.create.AppointmentCreateDTO;
import naderdeghaili.capstoneproject.repositories.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientService patientService;
    @Mock
    private UserService userService;
    @Mock
    private TreatmentPlanService treatmentPlanService;

    @InjectMocks
    private AppointmentService appointmentService;

    private User dentist;
    private User admin;
    private User otherDentist;
    private Patient patient;

    @BeforeEach
    void setUp() {
        dentist = new User("Mario", "Rossi", "dentist@test.it", "hashed", UserType.DENTIST);
        setId(dentist, UUID.randomUUID());

        otherDentist = new User("Luigi", "Verdi", "other@test.it", "hashed", UserType.DENTIST);
        setId(otherDentist, UUID.randomUUID());

        admin = new User("Admin", "User", "admin@test.it", "hashed", UserType.ADMIN);
        setId(admin, UUID.randomUUID());

        patient = new Patient("Luca", "Bianchi", LocalDate.of(1990, 1, 1),
                "BNCLCA90A01H501Z", "luca@test.it", "3331234567", "Via Roma 1");
        setId(patient, UUID.randomUUID());
    }

    @Test
    @DisplayName("findById with non-existent ID throws NotFoundException")
    void findById_notFound_throwsNotFoundException() {
        //ARRANGE
        UUID randomId = UUID.randomUUID();
        when(appointmentRepository.findById(randomId)).thenReturn(Optional.empty());
        //ACT & ASSERT
        assertThatThrownBy(() -> appointmentService.findById(randomId, dentist))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(randomId.toString());
    }

    @Test
    @DisplayName("DENTIST cannot access another dentist's appointment")
    void dentist_cannotAccessOtherDentistAppointment() {
        //ARRANGE
        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = new Appointment(patient, otherDentist, null,
                LocalDateTime.now().plusDays(1), 30, "Cleaning");
        setId(appointment, appointmentId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        //ACT & ASSERT
        assertThatThrownBy(() -> appointmentService.findById(appointmentId, dentist))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("ADMIN can access any appointment")
    void admin_canAccessAnyAppointment() {
        //ARRANGE
        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = new Appointment(patient, dentist, null,
                LocalDateTime.now().plusDays(1), 30, "Cleaning");
        setId(appointment, appointmentId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        //ACT
        Appointment result = appointmentService.findById(appointmentId, admin);
        //ASSERT
        assertThat(result.getId()).isEqualTo(appointmentId);
    }

    //helper
    private void setId(Object entity, UUID id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("setId failed on " + entity.getClass().getSimpleName(), e);
        }
    }

    @Nested
    @DisplayName("saveAppointment")
    class SaveAppointment {

        @Test
        @DisplayName("DENTIST creates appointment for themselves")
        void dentist_createsForSelf() {
            //ARRANGE
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
            AppointmentCreateDTO dto = new AppointmentCreateDTO(
                    patient.getId(), dentist.getId(), null, dateTime, 30, "Check-up");
            when(patientService.findById(patient.getId())).thenReturn(patient);
            when(appointmentRepository.existsByUser_IdAndDateTimeBetween(
                    any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));
            //ACT
            Appointment result = appointmentService.saveAppointment(dto, dentist);
            //ASSERT
            assertThat(result.getPatient()).isEqualTo(patient);
            assertThat(result.getUser()).isEqualTo(dentist);
            assertThat(result.getDuration()).isEqualTo(30);
        }

        @Test
        @DisplayName("Overlapping time slot throws IllegalArgumentException")
        void overlappingSlot_throws() {
            //ARRANGE
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
            AppointmentCreateDTO dto = new AppointmentCreateDTO(
                    patient.getId(), dentist.getId(), null, dateTime, 30, null);
            when(patientService.findById(patient.getId())).thenReturn(patient);
            when(appointmentRepository.existsByUser_IdAndDateTimeBetween(
                    any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);
            //ACT & ASSERT
            assertThatThrownBy(() -> appointmentService.saveAppointment(dto, dentist))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already has an appointment");
        }
    }
}