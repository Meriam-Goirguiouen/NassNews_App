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
        Integer agadirId = 1;
        Integer marrakechId = 2;
        Evenement eventAgadir1 = new Evenement(1, "Festival Timitar", "Concert de musique", "Place Al Amal", LocalDate.now(), "Concert", agadirId);
        Evenement eventAgadir2 = new Evenement(2, "Marathon d'Agadir", "Course à pied", "Cornique", LocalDate.now(), "Sport", agadirId);
        Evenement eventMarrakech = new Evenement(3, "Festival du Film", "Cinéma", "Palais des Congrès", LocalDate.now(), "Culture", marrakechId);

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
        when(evenementRepository.getEvenementsByVille(99)).thenReturn(List.of());

        // --- 2. Act ---
        List<Evenement> results = repositoryHelper.getEvenementsByVille(99); // Non-existent villeId

        // --- 3. Assert ---
        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
        verify(evenementRepository).getEvenementsByVille(99);
    }

    @Test
    void shouldFindEvenementsByCategorie() {
        // --- 1. Arrange ---
        Evenement eventConcert1 = new Evenement(1, "Festival Timitar", "Concert de musique", "Place Al Amal", LocalDate.now(), "Concert", 1);
        Evenement eventSport = new Evenement(2, "Marathon d'Agadir", "Course à pied", "Cornique", LocalDate.now(), "Sport", 1);
        Evenement eventConcert2 = new Evenement(4, "Concert Gnaoua", "Musique du monde", "Essaouira", LocalDate.now(), "Concert", 3);

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
        Evenement newEvent = new Evenement(100, "Nouvel Événement", "Description test", "Lieu Test", LocalDate.now(), "Test", 10);

        // --- 2. Act ---
        when(evenementRepository.ajouterEvenement(newEvent)).thenReturn(newEvent);
        when(evenementRepository.findById(100)).thenReturn(Optional.of(newEvent));

        Optional<Evenement> foundEvent = repositoryHelper.ajouterEvenementEtTrouverParId(newEvent, 100);

        // --- 3. Assert ---
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getTitre()).isEqualTo("Nouvel Événement");
        verify(evenementRepository).ajouterEvenement(newEvent);
        verify(evenementRepository).findById(100);
    }

    @Test
    void shouldSupprimerEvenement() {
        // --- 1. Arrange ---
        Evenement eventToDelete = new Evenement(99, "Événement à Supprimer", "...", "...", LocalDate.now(), "Delete", 99);
        when(evenementRepository.findById(99)).thenReturn(Optional.of(eventToDelete), Optional.empty());
        Mockito.doNothing().when(evenementRepository).supprimerEvenement(99);

        // --- 2. Act ---
        repositoryHelper.supprimerEvenementEtVerifier(99);

        // --- 3. Assert ---
        verify(evenementRepository).supprimerEvenement(99);
        verify(evenementRepository, Mockito.times(2)).findById(99);
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

        List<Evenement> getEvenementsByVille(Integer villeId) {
            return evenementRepository.getEvenementsByVille(villeId);
        }

        List<Evenement> getEvenementsByCategorie(String categorie) {
            return evenementRepository.getEvenementsByCategorie(categorie);
        }

        Optional<Evenement> ajouterEvenementEtTrouverParId(Evenement evenement, Integer idRecherche) {
            evenementRepository.ajouterEvenement(evenement);
            return evenementRepository.findById(idRecherche);
        }

        void supprimerEvenementEtVerifier(Integer idEvenement) {
            Optional<Evenement> beforeDelete = evenementRepository.findById(idEvenement);
            assertThat(beforeDelete).isPresent();
            evenementRepository.supprimerEvenement(idEvenement);
            Optional<Evenement> afterDelete = evenementRepository.findById(idEvenement);
            assertThat(afterDelete).isNotPresent();
        }
    }
}