package ma.nassnewsapp.backend.service;

import ma.nassnewsapp.backend.entities.Evenement;
import ma.nassnewsapp.backend.repositories.EvenementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Enables Mockito for JUnit 5
class EvenementServiceTest {

    @Mock // Creates a mock implementation of the repository
    private EvenementRepository evenementRepository;

    @InjectMocks // Creates an instance of EvenementService and injects the mocks into it
    private EvenementService evenementService;

    @Test
    void shouldReturnEvenementsWhenGetByVilleIsCalled() {
        // Given (Arrange)
        Evenement event1 = new Evenement(1, "Event A", "...", "Agadir", LocalDate.now(), "Concert", 1);
        given(evenementRepository.getEvenementsByVille(1)).willReturn(List.of(event1));

        // When (Act)
        List<Evenement> events = evenementService.getEvenementsByVille(1);

        // Then (Assert)
        assertThat(events).isNotNull();
        assertThat(events).hasSize(1);
        verify(evenementRepository).getEvenementsByVille(1); // Verify repository was called
    }

    @Test
    void shouldCreateEvenementSuccessfully() {
        // Given
        Evenement eventToCreate = new Evenement(100, "New Event", "...", "...", LocalDate.now(), "Test", 10);
        given(evenementRepository.ajouterEvenement(eventToCreate)).willReturn(eventToCreate);

        // When
        Evenement createdEvent = evenementService.createEvenement(eventToCreate);

        // Then
        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getTitre()).isEqualTo("New Event");
        verify(evenementRepository).ajouterEvenement(eventToCreate);
    }

    @Test
    void shouldReturnTrueWhenDeletingExistingEvent() {
        // Given
        Integer eventId = 1;
        given(evenementRepository.existsById(eventId)).willReturn(true);

        // When
        boolean isDeleted = evenementService.deleteEvenement(eventId);

        // Then
        assertThat(isDeleted).isTrue();
        verify(evenementRepository).supprimerEvenement(eventId); // Verify the delete method was called
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistingEvent() {
        // Given
        Integer eventId = 99;
        given(evenementRepository.existsById(eventId)).willReturn(false);

        // When
        boolean isDeleted = evenementService.deleteEvenement(eventId);

        // Then
        assertThat(isDeleted).isFalse();
    }
}
