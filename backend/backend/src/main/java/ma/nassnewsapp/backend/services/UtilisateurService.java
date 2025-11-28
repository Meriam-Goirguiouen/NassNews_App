package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Role;
import ma.nassnewsapp.backend.entities.Utilisateur;
import ma.nassnewsapp.backend.repositories.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);

    private final UtilisateurRepository utilisateurRepository;
    private final VilleService villeService;

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository, VilleService villeService) {
        this.utilisateurRepository = utilisateurRepository;
        this.villeService = villeService;
        logger.info("UtilisateurService initialized");
    }

    // ----------------------------------------------------------------------
    // GET ALL
    // ----------------------------------------------------------------------
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // ----------------------------------------------------------------------
    // GET BY ID
    // ----------------------------------------------------------------------
    public Optional<Utilisateur> getUtilisateurById(String id) {
        return utilisateurRepository.findById(id);
    }

    // ----------------------------------------------------------------------
    // GET BY EMAIL
    // ----------------------------------------------------------------------
    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    // ----------------------------------------------------------------------
    // CREATE USER (WITHOUT SECURITY)
    // ----------------------------------------------------------------------
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {

        // Validation villes favorites
        if (utilisateur.getVillesFavorites() != null && !utilisateur.getVillesFavorites().isEmpty()) {
            for (String villeIdInt : utilisateur.getVillesFavorites()) {
                villeService.getVilleById(String.valueOf(villeIdInt))
                    .orElseThrow(() -> new IllegalArgumentException("Ville with ID " + villeIdInt + " not found"));
            }
        }

        // Pas de hash ici — simple sauvegarde
        return utilisateurRepository.save(utilisateur);
    }

    // ----------------------------------------------------------------------
    // UPDATE USER
    // ----------------------------------------------------------------------
    public Optional<Utilisateur> updateUtilisateur(String id, Utilisateur utilisateurDetails) {

        if (utilisateurDetails.getVillesFavorites() != null && !utilisateurDetails.getVillesFavorites().isEmpty()) {
            for (String villeIdInt : utilisateurDetails.getVillesFavorites()) {
                villeService.getVilleById(String.valueOf(villeIdInt))
                    .orElseThrow(() -> new IllegalArgumentException("Ville with ID " + villeIdInt + " not found"));
            }
        }

        return utilisateurRepository.findById(id).map(existingUser -> {
            existingUser.setNom(utilisateurDetails.getNom());
            existingUser.setEmail(utilisateurDetails.getEmail());
            existingUser.setRole(utilisateurDetails.getRole());

            if (utilisateurDetails.getMotDePasse() != null && !utilisateurDetails.getMotDePasse().isEmpty()) {
                existingUser.setMotDePasse(utilisateurDetails.getMotDePasse());
            }

            existingUser.setVillesFavorites(utilisateurDetails.getVillesFavorites());

            return utilisateurRepository.save(existingUser);
        });
    }

    // ----------------------------------------------------------------------
    // DELETE USER
    // ----------------------------------------------------------------------
    public boolean deleteUtilisateur(String id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ----------------------------------------------------------------------
    // REGISTER (NO SECURITY)
    // ----------------------------------------------------------------------
    public Utilisateur registerUser(String nom, String email, String password, String confirmPassword) {

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords don't match!");
        }

        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists!");
        }

        String storedPassword = password;

        Utilisateur user = new Utilisateur(nom, email, storedPassword);
        user.setRole(Role.USER);

        return utilisateurRepository.save(user);
    }
    // ----------------------------------------------------------------------
    // LOGIN (NO SECURITY)
    // ----------------------------------------------------------------------
    public Utilisateur authenticateUser(String email, String password) {

        Optional<Utilisateur> optionalUser = utilisateurRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found!");
        }

        Utilisateur user = optionalUser.get();

        if (!password.equals(user.getMotDePasse())) {
            throw new IllegalArgumentException("Password incorrect!");
        }

        return user;
    }
}