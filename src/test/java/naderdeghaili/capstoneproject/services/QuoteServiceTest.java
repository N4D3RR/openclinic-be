package naderdeghaili.capstoneproject.services;

import naderdeghaili.capstoneproject.entities.*;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.create.QuoteCreateDTO;
import naderdeghaili.capstoneproject.payloads.update.QuoteUpdateDTO;
import naderdeghaili.capstoneproject.repositories.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;
    @Mock
    private PatientService patientService;
    @Mock
    private UserService userService;
    @Mock
    private TreatmentPlanService treatmentPlanService;

    @InjectMocks
    private QuoteService quoteService;

    private User dentist;
    private User admin;
    private User secretary;
    private Patient patient;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();

        dentist = new User("Mario", "Rossi", "dentist@test.it", "hashed", UserType.DENTIST);
        setId(dentist, UUID.randomUUID());

        secretary = new User("Segr", "Etaria", "secretary@test.it", "hashed", UserType.SECRETARY);
        setId(secretary, UUID.randomUUID());

        admin = new User("Admin", "User", "admin@test.it", "hashed", UserType.ADMIN);
        setId(admin, UUID.randomUUID());

        patient = new Patient("Luca", "Bianchi", LocalDate.of(1990, 1, 1),
                "BNCL CA90A01H501Z", "luca@test.it", "3331234567", "Via Roma 1");
        setId(patient, patientId);
    }


    @Test
    @DisplayName("find by ID")
    void findById_NotFound_ThrowsNotFoundException() {
        //ARRANGE
        UUID randomId = UUID.randomUUID();
        when(quoteRepository.findById(randomId))
                .thenReturn(Optional.empty());
        //ACT e ASSERT
        assertThatThrownBy(
                () -> quoteService.findById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Quote with id " + randomId + " not found");
    }

    @Test
    @DisplayName("DENTIST creates a quote for himself")
    void dentist_createsQuoteForHimself() {
        //ARRANGE
        QuoteCreateDTO dto = new QuoteCreateDTO(patientId, null, "Note test");
        when(patientService.findById(patientId)).thenReturn(patient);
        when(quoteRepository.save(any(Quote.class))).thenAnswer(i -> i.getArgument(0));

        //ACT
        Quote result = quoteService.saveQuote(dto, dentist);

        //ASSERT
        assertThat(result.getDentist()).isEqualTo(dentist);
        assertThat(result.getPatient()).isEqualTo(patient);
        assertThat(result.getStatus()).isEqualTo(QuoteStatus.DRAFT);
        verify(quoteRepository).save(any(Quote.class));
    }

    @Test
    @DisplayName("SECRETARY who creates quote throws IllegalArgumentException")
    void secretary_creatingQuote_throws() {
        //ARRANGE
        QuoteCreateDTO dto = new QuoteCreateDTO(patientId, null, null);
        when(patientService.findById(patientId)).thenReturn(patient);
        //ACT e ASSERT
        assertThatThrownBy(() -> quoteService.saveQuote(dto, secretary))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only a DENTIST can create quotes");
    }

    @Test
    @DisplayName("ADMIN assigning a quote to a non-DENTIST throws IllegalArgumentException")
    void admin_assigningToNonDentist_throws() {
        //ARRANGE
        UUID secretaryId = secretary.getId();
        QuoteCreateDTO dto = new QuoteCreateDTO(patientId, secretaryId, null);
        when(patientService.findById(patientId)).thenReturn(patient);
        when(userService.findByID(secretaryId)).thenReturn(secretary);
        //ACT e ASSERT
        assertThatThrownBy(() -> quoteService.saveQuote(dto, admin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Assigned user must be a DENTIST");
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
    @DisplayName("findByIdAndUpdate")
    class FindByIdAndUpdate {

        private Quote draftQuote;
        private UUID quoteId;

        @BeforeEach
        void setUpQuote() {
            quoteId = UUID.randomUUID();
            draftQuote = new Quote(QuoteStatus.DRAFT, "Original notes", patient, dentist);
            setId(draftQuote, quoteId);
            when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(draftQuote));
        }

        @Test
        @DisplayName("note update on DRAFT works correctly")
        void updateNotes_onDraft_works() {
            //ARRANGE
            QuoteUpdateDTO dto = new QuoteUpdateDTO(null, "Notes updated");
            when(quoteRepository.save(any(Quote.class))).thenAnswer(i -> i.getArgument(0));
            //ACT
            Quote result = quoteService.findByIdAndUpdate(quoteId, dto, dentist);
            //ASSERT
            assertThat(result.getNotes()).isEqualTo("Notes updated");
            assertThat(result.getStatus()).isEqualTo(QuoteStatus.DRAFT);
        }

        @Test
        @DisplayName("Accept with items creates TreatmentPlan")
        void accept_withItems_createsTreatmentPlan() {
            //ARRANGE
            QuoteItem item = mock(QuoteItem.class);
            draftQuote.getItems().add(item);
            QuoteUpdateDTO dto = new QuoteUpdateDTO(QuoteStatus.ACCEPTED, null);
            when(treatmentPlanService.existsByQuoteId(quoteId)).thenReturn(false);
            when(quoteRepository.save(any(Quote.class))).thenAnswer(i -> i.getArgument(0));
            //ACT
            Quote result = quoteService.findByIdAndUpdate(quoteId, dto, dentist);
            //ASSERT
            assertThat(result.getStatus()).isEqualTo(QuoteStatus.ACCEPTED);
            verify(treatmentPlanService).createFromQuote(draftQuote);
        }
    }
}


