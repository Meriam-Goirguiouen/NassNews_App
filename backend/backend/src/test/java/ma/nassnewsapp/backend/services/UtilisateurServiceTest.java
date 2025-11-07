package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Utilisateur;
import ma.nassnewsapp.backend.repositories.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Active l'utilisation de Mockito
public class UtilisateurServiceTest {

    @Mock // On demande à Mockito de créer une fausse version de UtilisateurRepository
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks // On crée une vraie instance de UtilisateurService, et Mockito va lui injecter le @Mock ci-dessus
    private UtilisateurService utilisateurService;

    @Test
    void shouldReturnUserWhenFoundById() {
        // --- 1. Préparation (Arrange) ---
        // On crée un faux utilisateur
        Utilisateur fakeUser = new Utilisateur();
        fakeUser.setIdUtilisateur(1);
        fakeUser.setNom("Test User");

        // On définit le comportement de notre faux repository :
        // "QUAND la méthode findById(1) sera appelée, ALORS retourne notre faux utilisateur"
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(fakeUser));

        // --- 2. Action (Act) ---
        // On appelle la méthode du service que l'on veut tester
        Optional<Utilisateur> result = utilisateurService.getUtilisateurById(1);

        // --- 3. Vérification (Assert) ---
        // On vérifie que le service a retourné le bon résultat
        assertThat(result).isPresent();
        assertThat(result.get().getNom()).isEqualTo("Test User");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // --- 1. Préparation (Arrange) ---
        Utilisateur userToCreate = new Utilisateur();
        userToCreate.setNom("New User");
        userToCreate.setEmail("new@test.com");
        
        // On crée l'utilisateur qui est censé être retourné par la base de données après sauvegarde
        Utilisateur savedUser = new Utilisateur();
        savedUser.setIdUtilisateur(100); // Imaginons que la BDD lui a donné un ID
        savedUser.setNom("New User");
        savedUser.setEmail("new@test.com");

        // On définit le comportement du faux repository :
        // "QUAND la méthode save() sera appelée avec n'importe quel objet Utilisateur,
        // ALORS retourne notre 'savedUser'."
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(savedUser);

        // --- 2. Action (Act) ---
        Utilisateur result = utilisateurService.createUtilisateur(userToCreate);

        // --- 3. Vérification (Assert) ---
        assertThat(result).isNotNull();
        assertThat(result.getIdUtilisateur()).isEqualTo(100);
        assertThat(result.getNom()).isEqualTo("New User");
    }
}
