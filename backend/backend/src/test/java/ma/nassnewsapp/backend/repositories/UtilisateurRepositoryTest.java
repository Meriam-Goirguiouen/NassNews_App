package ma.nassnewsapp.backend.repositories;

import ma.nassnewsapp.backend.entities.Utilisateur;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest // Annotation clé ! Elle prépare un environnement de test pour MongoDB.
public class UtilisateurRepositoryTest {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @AfterEach
    void tearDown() {
        // Vider la base de données de test après chaque test pour les isoler.
        this.utilisateurRepository.deleteAll();
    }

    @Test
    void shouldFindUserByEmailWhenUserExists() {
        // --- 1. Préparation (Arrange) ---
        // Créez des utilisateurs de test
        String emailToFind = "karim.alami@test.com";
        
        Utilisateur user1 = new Utilisateur();
        user1.setIdUtilisateur(1);
        user1.setNom("Karim Alami");
        user1.setEmail(emailToFind);
        user1.setMotDePasse("pass123");
        user1.setRole("UTILISATEUR");

        Utilisateur user2 = new Utilisateur();
        user2.setIdUtilisateur(2);
        user2.setNom("Fatima Benali");
        user2.setEmail("fatima.benali@test.com");
        user2.setMotDePasse("pass456");
        user2.setRole("ADMIN_COMMUNAL");

        // Enregistrez-les dans la base de données de test
        utilisateurRepository.saveAll(List.of(user1, user2));

        // --- 2. Action (Act) ---
        // Appelez la méthode que vous voulez tester
        Optional<Utilisateur> resultat = utilisateurRepository.findByEmail(emailToFind);

        // --- 3. Vérification (Assert) ---
        // Vérifiez que le résultat est correct
        assertThat(resultat).isPresent(); // On vérifie que l'utilisateur a été trouvé
        assertThat(resultat.get().getNom()).isEqualTo("Karim Alami");
    }

    @Test
    void shouldSaveAndRetrieveUserWithFavoriteCities() {
        // --- 1. Préparation (Arrange) ---
        // Créez un utilisateur avec une liste de villes favorites
        Utilisateur userWithFavorites = new Utilisateur();
        userWithFavorites.setIdUtilisateur(3);
        userWithFavorites.setNom("Samir Saadi");
        userWithFavorites.setEmail("samir.saadi@test.com");
        userWithFavorites.setMotDePasse("pass789");
        userWithFavorites.setRole("UTILISATEUR");
        userWithFavorites.setVillesFavorites(List.of(101, 202, 303)); // Ex: Casablanca, Rabat, Marrakech

        // Enregistrez-le dans la base de données de test
        utilisateurRepository.save(userWithFavorites);

        // --- 2. Action (Act) ---
        // Récupérez l'utilisateur par son ID pour vérifier que tout est bien sauvegardé
        Optional<Utilisateur> resultat = utilisateurRepository.findById(3);

        // --- 3. Vérification (Assert) ---
        // Vérifiez que le résultat est correct
        assertThat(resultat).isPresent();
        
        Utilisateur retrievedUser = resultat.get();
        assertThat(retrievedUser.getNom()).isEqualTo("Samir Saadi");
        assertThat(retrievedUser.getVillesFavorites()).isNotNull();
        assertThat(retrievedUser.getVillesFavorites()).hasSize(3);
        assertThat(retrievedUser.getVillesFavorites()).containsExactlyInAnyOrder(101, 202, 303);
    }
}