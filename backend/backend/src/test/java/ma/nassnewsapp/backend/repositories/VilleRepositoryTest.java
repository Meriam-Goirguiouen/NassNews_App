package ma.nassnewsapp.backend.repositories;

import ma.nassnewsapp.backend.entities.Ville;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


@DataMongoTest
public class VilleRepositoryTest {
    
    @Autowired
    private VilleRepository villeRepository;

    @AfterEach
    void tearDown() {
        // Vider la base de données de test après chaque test pour les isoler.
        this.villeRepository.deleteAll();
    }

    @Test
    void testCreateAndFindVille() {
        Ville ville1 = new Ville();
        ville1.setNom("Agadir");
        // ville1.setRegion("Sous-Massa");
        ville1.setCoordonnees("48.8566,2.3522");
        // ville1.setPopulation(2148000L);

        Ville savedVille = villeRepository.save(ville1);
        // Vérification que la ville est sauvegardée
        Optional<Ville> foundVille = villeRepository.findById(savedVille.getId());
        assertThat(foundVille).isPresent();
        assertThat(foundVille.get().getNom()).isEqualTo("Agadir");

        // Rechercher la ville par nom
        Optional<Ville> foundByName = villeRepository.findByNom("Agadir");
        assertThat(foundByName).isPresent();
        // assertThat(foundByName.get().getRegion()).isEqualTo("Sous-Massa");
    }

    @Test
    void testDeleteVille(){
        Ville ville2 = new Ville();
        ville2.setNom("Marrakech");
        // ville2.setRegion("Marrakech-Safi");
        ville2.setCoordonnees("45.7640,4.8357");

        Ville saveMarrakech = villeRepository.save(ville2);
        // Suppression 
        villeRepository.deleteById(saveMarrakech.getId());

        Optional<Ville> deleted = villeRepository.findById(saveMarrakech.getId());
        assertThat(deleted).isEmpty(); 
    }
}
