package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Utilisateur;
import ma.nassnewsapp.backend.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    // L'injection par constructeur est une bonne pratique.
    // Spring s'occupe de fournir une instance de UtilisateurRepository.
    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Récupère tous les utilisateurs.
     * @return une liste de tous les utilisateurs.
     */
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Récupère un utilisateur par son identifiant unique.
     * @param id L'identifiant de l'utilisateur.
     * @return un Optional contenant l'utilisateur s'il est trouvé, sinon un Optional vide.
     */
    public Optional<Utilisateur> getUtilisateurById(Integer id) {
        return utilisateurRepository.findById(id);
    }

    /**
     * Récupère un utilisateur par son adresse e-mail.
     * @param email L'e-mail de l'utilisateur.
     * @return un Optional contenant l'utilisateur s'il est trouvé.
     */
    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    /**
     * Crée et sauvegarde un nouvel utilisateur.
     * NOTE: Dans une application réelle, il faudrait hasher le mot de passe ici !
     * @param utilisateur L'objet utilisateur à créer.
     * @return L'utilisateur sauvegardé.
     */
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        // Logique de validation ou de traitement avant sauvegarde (ex: hasher le mot de passe)
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Met à jour les informations d'un utilisateur existant.
     * @param id L'identifiant de l'utilisateur à mettre à jour.
     * @param utilisateurDetails L'objet contenant les nouvelles informations.
     * @return un Optional contenant l'utilisateur mis à jour s'il existait.
     */
    public Optional<Utilisateur> updateUtilisateur(Integer id, Utilisateur utilisateurDetails) {
        return utilisateurRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setNom(utilisateurDetails.getNom());
                    existingUser.setEmail(utilisateurDetails.getEmail());
                    existingUser.setRole(utilisateurDetails.getRole());
                    // On ne met généralement pas à jour le mot de passe de cette manière.
                    // Il faudrait une méthode dédiée comme "changePassword".
                    if (utilisateurDetails.getMotDePasse() != null && !utilisateurDetails.getMotDePasse().isEmpty()) {
                         existingUser.setMotDePasse(utilisateurDetails.getMotDePasse()); 
                    }
                    existingUser.setVillesFavorites(utilisateurDetails.getVillesFavorites());
                    return utilisateurRepository.save(existingUser);
                });
    }

    /**
     * Supprime un utilisateur par son identifiant.
     * @param id L'identifiant de l'utilisateur à supprimer.
     * @return true si la suppression a réussi, false si l'utilisateur n'existait pas.
     */
    public boolean deleteUtilisateur(Integer id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return true;
        }
        return false;
    }
}