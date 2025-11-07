package ma.nassnewsapp.backend.repositories;

import ma.nassnewsapp.backend.entities.Actualite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Date; 
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest // Annotation clé ! Elle prépare un environnement de test pour MongoDB.
public class ActualiteRepositoryTest {

    @Autowired
    private ActualiteRepository actualiteRepository;

    @AfterEach
    void tearDown() {
        // Vider la base de données de test après chaque test pour les isoler.
        this.actualiteRepository.deleteAll();
    }

    @Test
    void shouldFindActualitesByVilleId() {
        // --- 1. Préparation (Arrange) ---
        // Créez des données de test
        String agadirId = "ville_agadir_123";
        String marrakechId = "ville_marrakech_456";

        
        Actualite actualiteAgadir1 = new Actualite("Titre 1 Agadir", "Contenu de l'article 1", new Date(), "Source Test", "Catégorie A", agadirId);
        Actualite actualiteAgadir2 = new Actualite("Titre 2 Agadir", "Contenu de l'article 2", new Date(), "Source Test", "Catégorie B", agadirId);
        Actualite actualiteMarrakech = new Actualite("Titre 1 Marrakech", "Contenu de l'article 3", new Date(), "Source Test", "Catégorie A", marrakechId);

        // Enregistrez-les dans la base de données de test
        actualiteRepository.saveAll(List.of(actualiteAgadir1, actualiteAgadir2, actualiteMarrakech));

        // --- 2. Action (Act) ---
        // Appelez la méthode que vous voulez tester
        List<Actualite> resultats = actualiteRepository.findByVilleId(agadirId);

        // --- 3. Vérification (Assert) ---
        // Vérifiez que le résultat est correct
        assertThat(resultats).isNotNull();
        assertThat(resultats).hasSize(2); // On s'attend à trouver 2 actualités pour Agadir
        assertThat(resultats).extracting(Actualite::getTitre).contains("Titre 1 Agadir", "Titre 2 Agadir");
        assertThat(resultats).extracting(Actualite::getTitre).doesNotContain("Titre 1 Marrakech");
    }

    @Test
    void shouldFindActualitesByCategorie() {
        // --- 1. Préparation (Arrange) ---
        String agadirId = "ville_agadir_123";
        String marrakechId = "ville_marrakech_456";

        
        Actualite actualiteA = new Actualite("Titre 1 Agadir", "Contenu de l'article 1", new Date(), "Source Test", "Catégorie A", agadirId);
        Actualite actualiteB = new Actualite("Titre 2 Agadir", "Contenu de l'article 2", new Date(), "Source Test", "Catégorie B", agadirId);
        Actualite actualiteMarrakechA = new Actualite("Titre 1 Marrakech", "Contenu de l'article 3", new Date(), "Source Test", "Catégorie A", marrakechId);

        actualiteRepository.saveAll(List.of(actualiteA, actualiteB, actualiteMarrakechA));

        // --- 2. Action (Act) ---
        List<Actualite> resultats = actualiteRepository.findByCategorie("Catégorie A");

        // --- 3. Vérification (Assert) ---
        assertThat(resultats).isNotNull();
        assertThat(resultats).hasSize(2); // On s'attend à 2 actualités de Catégorie A
        assertThat(resultats).extracting(Actualite::getTitre).contains("Titre 1 Agadir", "Titre 1 Marrakech");
    }
}