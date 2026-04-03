package naderdeghaili.capstoneproject.services;

import naderdeghaili.capstoneproject.entities.*;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.repositories.TreatmentPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TreatmentPlanServiceTest {

    @Mock
    private TreatmentPlanRepository treatmentPlanRepository;
    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private TreatmentPlanService treatmentPlanService;

    private User dentist;
    private Patient patient;
    private Procedure procedure;

    @BeforeEach
    void setUp() {
        dentist = new User("Mario", "Rossi", "dentist@test.it", "hashed", UserType.DENTIST);
        setId(dentist, UUID.randomUUID());

        patient = new Patient("Luca", "Bianchi", LocalDate.of(1990, 1, 1),
                "BNCLCA90A01H501Z", "luca@test.it", "3331234567", "Via Roma 1");
        setId(patient, UUID.randomUUID());

        procedure = new Procedure("D001", "Filling", "Composite filling", 30, new BigDecimal("120.00"));
        setId(procedure, UUID.randomUUID());
    }

    @Test
    @DisplayName("findById with non-existent ID throws NotFoundException")
    void findById_notFound_throwsNotFoundException() {
        //ARRANGE
        UUID randomId = UUID.randomUUID();
        when(treatmentPlanRepository.findById(randomId)).thenReturn(Optional.empty());
        //ACT & ASSERT
        assertThatThrownBy(() -> treatmentPlanService.findById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(randomId.toString());
    }

    @Test
    @DisplayName("createFromQuote calculates total amount from quote items")
    void createFromQuote_calculatesTotal() {
        //ARRANGE
        Quote quote = new Quote(QuoteStatus.ACCEPTED, "Test", patient, dentist);
        setId(quote, UUID.randomUUID());

        QuoteItem item1 = new QuoteItem(11, new BigDecimal("150.00"), quote, procedure);
        QuoteItem item2 = new QuoteItem(21, new BigDecimal("200.00"), quote, procedure);
        quote.getItems().add(item1);
        quote.getItems().add(item2);

        when(treatmentPlanRepository.save(any(TreatmentPlan.class))).thenAnswer(i -> i.getArgument(0));
        //ACT
        TreatmentPlan result = treatmentPlanService.createFromQuote(quote);
        //ASSERT
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("350.00"));
        assertThat(result.getStatus()).isEqualTo(TreatmentPlanStatus.IN_PROGRESS);
        assertThat(result.getQuote()).isEqualTo(quote);
    }

    @Test
    @DisplayName("createFromQuote sets expected end date based on procedure durations")
    void createFromQuote_setsExpectedEndDate() {
        //ARRANGE
        Quote quote = new Quote(QuoteStatus.ACCEPTED, null, patient, dentist);
        setId(quote, UUID.randomUUID());

        //30 min procedure → totalMinutes = 30 → max(1, 30/60) = 1 week
        QuoteItem item = new QuoteItem(11, new BigDecimal("100.00"), quote, procedure);
        quote.getItems().add(item);

        when(treatmentPlanRepository.save(any(TreatmentPlan.class))).thenAnswer(i -> i.getArgument(0));
        //ACT
        TreatmentPlan result = treatmentPlanService.createFromQuote(quote);
        //ASSERT
        assertThat(result.getExpectedEndDate()).isEqualTo(LocalDate.now().plusWeeks(1));
        assertThat(result.getStartDate()).isEqualTo(LocalDate.now());
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
}