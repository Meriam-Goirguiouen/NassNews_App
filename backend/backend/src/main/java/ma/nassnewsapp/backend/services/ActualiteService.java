package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Actualite;
import ma.nassnewsapp.backend.repositories.ActualiteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class ActualiteService {

    private final ActualiteRepository actualiteRepository;

    // Spring va automatiquement injecter votre repository ici
    public ActualiteService(ActualiteRepository actualiteRepository) {
        this.actualiteRepository = actualiteRepository;
    }

    /**
     * Récupère toutes les actualités pour un ID de ville donné.
     * @param villeId L'ID de la ville.
     * @return Une liste d'actualités.
     */
    public List<Actualite> getActualitesByVille(String villeId) {
        // Pour l'instant, on appelle directement le repository.
        // Plus tard, on pourra ajouter des validations ou de la logique ici.
        return actualiteRepository.findByVilleId(villeId);
    }

    /**
     * Récupère toutes les actualités pour une catégorie donnée.
     * @param categorie La catégorie à rechercher.
     * @return Une liste d'actualités.
     */
    public List<Actualite> getActualitesByCategorie(String categorie) {
        return actualiteRepository.findByCategorie(categorie);
    }

    public List<Actualite> getAllActualites() {
    return actualiteRepository.findAll();
    }

    public Optional<Actualite> getActualiteById(String id) {
    return actualiteRepository.findById(id);
    }
}