package ma.nassnewsapp.backend.repositories;

import ma.nassnewsapp.backend.entities.Utilisateur;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends MongoRepository<Utilisateur, String> {

    // Spring Data MongoDB va automatiquement générer une implémentation pour cette méthode
    // Elle trouvera un utilisateur par son adresse e-mail.
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    // boolean existsByUsername(String nom);

}