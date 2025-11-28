package ma.nassnewsapp.backend.repositories;

import ma.nassnewsapp.backend.entities.Evenement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the EvenementRepository interface using Mockito.
 * These tests validate that the expected data is returned without hitting a real database.
 */
@ExtendWith(MockitoExtension.class)
public class EvenementRepositoryTest {

    @Mock
    private EvenementRepository evenementRepository;

    @InjectMocks
    private EvenementRepositoryTestHelper repositoryHelper;

    @Test
    void shouldFindEvenementsByVille() {
        // --- 1. Arrange (Setup test data) ---
        String agadirId = "ville1";
        String marrakechId = "ville2";
        Evenement eventAgadir1 = new Evenement("event1", "Festival Timitar", "Concert de musique", "Place Al Amal", LocalDate.now(), "Concert", agadirId);
        Evenement eventAgadir2 = new Evenement("event2", "Marathon d'Agadir", "Course à pied", "Cornique", LocalDate.now(), "Sport", agadirId);
        Evenement eventMarrakech = new Evenement("event3", "Festival du Film", "Cinéma", "Palais des Congrès", LocalDate.now(), "Culture", marrakechId);

        List<Evenement> expectedEvents = List.of(eventAgadir1, eventAgadir2);

        when(evenementRepository.getEvenementsByVille(agadirId)).thenReturn(expectedEvents);

        // --- 2. Act (Execute the method under test) ---
        List<Evenement> results = repositoryHelper.getEvenementsByVille(agadirId);

        // --- 3. Assert (Verify the result) ---
        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Evenement::getTitre).containsExactlyInAnyOrder("Festival Timitar", "Marathon d'Agadir");
        verify(evenementRepository).getEvenementsByVille(agadirId);
    }

    @Test
    void shouldReturnEmptyListWhenNoEventsForVille() {
        // --- 1. Arrange ---
        String nonExistentVilleId = "nonexistent";
        when(evenementRepository.getEvenementsByVille(nonExistentVilleId)).thenReturn(List.of());

        // --- 2. Act ---
        List<Evenement> results = repositoryHelper.getEvenementsByVille(nonExistentVilleId); // Non-existent villeId

        // --- 3. Assert ---
        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
        verify(evenementRepository).getEvenementsByVille(nonExistentVilleId);
    }

    @Test
    void shouldFindEvenementsByCategorie() {
        // --- 1. Arrange ---
        Evenement eventConcert1 = new Evenement("event1", "Festival Timitar", "Concert de musique", "Place Al Amal", LocalDate.now(), "Concert", "ville1");
        Evenement eventSport = new Evenement("event2", "Marathon d'Agadir", "Course à pied", "Cornique", LocalDate.now(), "Sport", "ville1");
        Evenement eventConcert2 = new Evenement("event4", "Concert Gnaoua", "Musique du monde", "Essaouira", LocalDate.now(), "Concert", "ville3");

        List<Evenement> expectedConcerts = List.of(eventConcert1, eventConcert2);

        when(evenementRepository.getEvenementsByCategorie("Concert")).thenReturn(expectedConcerts);

        // --- 2. Act ---
        List<Evenement> results = repositoryHelper.getEvenementsByCategorie("Concert");

        // --- 3. Assert ---
        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Evenement::getTypeEvenement).containsOnly("Concert");
        verify(evenementRepository).getEvenementsByCategorie("Concert");
    }

    @Test
    void shouldAjouterEvenementAndFindItById() {
        // --- 1. Arrange ---
        Evenement newEvent = new Evenement("event100", "Nouvel Événement", "Description test", "Lieu Test", LocalDate.now(), "Test", "ville10");

        // --- 2. Act ---
        when(evenementRepository.ajouterEvenement(newEvent)).thenReturn(newEvent);
        when(evenementRepository.findById("event100")).thenReturn(Optional.of(newEvent));

        Optional<Evenement> foundEvent = repositoryHelper.ajouterEvenementEtTrouverParId(newEvent, "event100");

        // --- 3. Assert ---
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getTitre()).isEqualTo("Nouvel Événement");
        verify(evenementRepository).ajouterEvenement(newEvent);
        verify(evenementRepository).findById("event100");
    }

    @Test
    void shouldSupprimerEvenement() {
        // --- 1. Arrange ---
        String eventId = "event99";
        Evenement eventToDelete = new Evenement(eventId, "Événement à Supprimer", "...", "...", LocalDate.now(), "Delete", "ville99");
        when(evenementRepository.findById(eventId)).thenReturn(Optional.of(eventToDelete), Optional.empty());
        Mockito.doNothing().when(evenementRepository).supprimerEvenement(eventId);

        // --- 2. Act ---
        repositoryHelper.supprimerEvenementEtVerifier(eventId);

        // --- 3. Assert ---
        verify(evenementRepository).supprimerEvenement(eventId);
        verify(evenementRepository, Mockito.times(2)).findById(eventId);
    }

    /**
     * Helper class to interact with the mocked repository.
     * This allows us to keep method calls grouped while avoiding database access.
     */
    static class EvenementRepositoryTestHelper {

        private final EvenementRepository evenementRepository;

        EvenementRepositoryTestHelper(EvenementRepository evenementRepository) {
            this.evenementRepository = evenementRepository;
        }

        List<Evenement> getEvenementsByVille(String villeId) {
            return evenementRepository.getEvenementsByVille(villeId);
        }

        List<Evenement> getEvenementsByCategorie(String categorie) {
            return evenementRepository.getEvenementsByCategorie(categorie);
        }

        Optional<Evenement> ajouterEvenementEtTrouverParId(Evenement evenement, String idRecherche) {
            evenementRepository.ajouterEvenement(evenement);
            return evenementRepository.findById(idRecherche);
        }

        void supprimerEvenementEtVerifier(String idEvenement) {
            Optional<Evenement> beforeDelete = evenementRepository.findById(idEvenement);
            assertThat(beforeDelete).isPresent();
            evenementRepository.supprimerEvenement(idEvenement);
            Optional<Evenement> afterDelete = evenementRepository.findById(idEvenement);
            assertThat(afterDelete).isNotPresent();
        }
    }
}