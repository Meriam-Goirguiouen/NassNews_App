package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Actualite;
import ma.nassnewsapp.backend.repositories.ActualiteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ActualiteService {

    private final ActualiteRepository actualiteRepository;
    private final VilleService villeService;

    // Spring va automatiquement injecter votre repository ici
    public ActualiteService(ActualiteRepository actualiteRepository, VilleService villeService) {
        this.actualiteRepository = actualiteRepository;
        this.villeService = villeService;
    }

    /**
     * Récupère toutes les actualités pour un ID de ville donné.
     * @param villeId L'ID de la ville.
     * @return Une liste d'actualités.
     */
    public List<Actualite> getActualitesByVille(String villeId) {
        // Validate ville exists
        villeService.getVilleById(villeId)
            .orElseThrow(() -> new IllegalArgumentException("Ville with ID " + villeId + " not found"));
        return actualiteRepository.findByVilleId(villeId);
    }
    
    /**
     * Crée une nouvelle actualité avec validation de la ville.
     * @param actualite L'actualité à créer.
     * @return L'actualité créée.
     */
    public Actualite createActualite(Actualite actualite) {
        // Validate ville exists if villeId is provided
        if (actualite.getVilleId() != null && !actualite.getVilleId().isEmpty()) {
            villeService.getVilleById(actualite.getVilleId())
                .orElseThrow(() -> new IllegalArgumentException("Ville with ID " + actualite.getVilleId() + " not found"));
        }
        return actualiteRepository.save(actualite);
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