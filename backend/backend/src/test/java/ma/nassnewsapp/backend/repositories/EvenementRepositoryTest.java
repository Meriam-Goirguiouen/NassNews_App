package ma.nassnewsapp.backend.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import ma.nassnewsapp.backend.entities.Evenement;

@DataMongoTest
class EvenementRepositoryTest {

    @Autowired
    private EvenementRepository evenementRepository;

    private Evenement evenement1;
    private Evenement evenement2;
    private Evenement evenement3;

    @BeforeEach
    void setUp() {
        evenementRepository.deleteAll();

        // Créer des événements de test
        evenement1 = new Evenement();
        evenement1.setIdEvenement(1);
        evenement1.setTitre("Festival de Musique");
        evenement1.setDescription("Un grand festival de musique en plein air");
        evenement1.setLieu("Place Jemaa el-Fnaa");
        evenement1.setDateEvenement(LocalDate.of(2024, 6, 15));
        evenement1.setTypeEvenement("Culture");
        evenement1.setVilleId(1);

        evenement2 = new Evenement();
        evenement2.setIdEvenement(2);
        evenement2.setTitre("Match de Football");
        evenement2.setDescription("Match de football local");
        evenement2.setLieu("Stade Municipal");
        evenement2.setDateEvenement(LocalDate.of(2024, 7, 20));
        evenement2.setTypeEvenement("Sport");
        evenement2.setVilleId(1);

        evenement3 = new Evenement();
        evenement3.setIdEvenement(3);
        evenement3.setTitre("Exposition d'Art");
        evenement3.setDescription("Exposition d'art contemporain");
        evenement3.setLieu("Musée d'Art");
        evenement3.setDateEvenement(LocalDate.of(2024, 8, 10));
        evenement3.setTypeEvenement("Culture");
        evenement3.setVilleId(2);
    }

    @Test
    void testAjouterEvenement() {
        Evenement saved = evenementRepository.ajouterEvenement(evenement1);

        assertNotNull(saved);
        assertNotNull(saved.getIdEvenement());
        assertEquals("Festival de Musique", saved.getTitre());
        assertEquals(1, saved.getVilleId());
        assertEquals("Culture", saved.getTypeEvenement());
        assertTrue(evenementRepository.existsById(saved.getIdEvenement()));
    }

    @Test
    void testModifierEvenement() {
        Evenement saved = evenementRepository.ajouterEvenement(evenement1);
        Integer id = saved.getIdEvenement();

        saved.setTitre("Festival de Musique Modifié");
        saved.setDescription("Description modifiée");
        Evenement updated = evenementRepository.modifierEvenement(saved);

        assertNotNull(updated);
        assertEquals(id, updated.getIdEvenement());
        assertEquals("Festival de Musique Modifié", updated.getTitre());
        assertEquals("Description modifiée", updated.getDescription());
    }

    @Test
    void testSupprimerEvenement() {
        Evenement saved = evenementRepository.ajouterEvenement(evenement1);
        Integer id = saved.getIdEvenement();

        assertTrue(evenementRepository.existsById(id));

        evenementRepository.supprimerEvenement(id);

        assertFalse(evenementRepository.existsById(id));
    }

    @Test
    void testGetEvenementsByVille() {
        evenementRepository.ajouterEvenement(evenement1);
        evenementRepository.ajouterEvenement(evenement2);
        evenementRepository.ajouterEvenement(evenement3);

        List<Evenement> evenementsVille1 = evenementRepository.getEvenementsByVille(1);
        assertNotNull(evenementsVille1);
        assertEquals(2, evenementsVille1.size());
        assertTrue(evenementsVille1.stream().allMatch(e -> e.getVilleId().equals(1)));

        List<Evenement> evenementsVille2 = evenementRepository.getEvenementsByVille(2);
        assertNotNull(evenementsVille2);
        assertEquals(1, evenementsVille2.size());
        assertEquals(2, evenementsVille2.get(0).getVilleId());
    }

    @Test
    void testGetEvenementsByCategorie() {
        evenementRepository.ajouterEvenement(evenement1);
        evenementRepository.ajouterEvenement(evenement2);
        evenementRepository.ajouterEvenement(evenement3);

        List<Evenement> evenementsCulture = evenementRepository.getEvenementsByCategorie("Culture");
        assertNotNull(evenementsCulture);
        assertEquals(2, evenementsCulture.size());
        assertTrue(evenementsCulture.stream().allMatch(e -> "Culture".equals(e.getTypeEvenement())));

        List<Evenement> evenementsSport = evenementRepository.getEvenementsByCategorie("Sport");
        assertNotNull(evenementsSport);
        assertEquals(1, evenementsSport.size());
        assertEquals("Sport", evenementsSport.get(0).getTypeEvenement());
    }

    @Test
    void testGetEvenementsByVille_NoResults() {
        List<Evenement> evenements = evenementRepository.getEvenementsByVille(999);
        assertNotNull(evenements);
        assertTrue(evenements.isEmpty());
    }

    @Test
    void testGetEvenementsByCategorie_NoResults() {
        List<Evenement> evenements = evenementRepository.getEvenementsByCategorie("Inexistante");
        assertNotNull(evenements);
        assertTrue(evenements.isEmpty());
    }

    @Test
    void testFindAll() {
        evenementRepository.ajouterEvenement(evenement1);
        evenementRepository.ajouterEvenement(evenement2);
        evenementRepository.ajouterEvenement(evenement3);

        List<Evenement> allEvenements = evenementRepository.findAll();
        assertNotNull(allEvenements);
        assertEquals(3, allEvenements.size());
    }
}
