package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Actualite;
import ma.nassnewsapp.backend.repositories.ActualiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ActualiteService {

    private static final Logger logger = LoggerFactory.getLogger(ActualiteService.class);
    
    private final ActualiteRepository actualiteRepository;
    private final VilleService villeService;

    // Spring va automatiquement injecter votre repository ici
    public ActualiteService(ActualiteRepository actualiteRepository, VilleService villeService) {
        this.actualiteRepository = actualiteRepository;
        this.villeService = villeService;
        logger.info("ActualiteService initialized with VilleService dependency");
    }

    /**
     * Récupère toutes les actualités pour un ID de ville donné.
     * @param villeId L'ID de la ville.
     * @return Une liste d'actualités.
     */
    public List<Actualite> getActualitesByVille(String villeId) {
        // Validate ville exists
        logger.debug("ActualiteService: Validating ville ID {} via VilleService", villeId);
        villeService.getVilleById(villeId)
            .orElseThrow(() -> {
                logger.warn("ActualiteService: Ville with ID {} not found via VilleService", villeId);
                return new IllegalArgumentException("Ville with ID " + villeId + " not found");
            });
        logger.debug("ActualiteService: Ville validation successful via VilleService");
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
            logger.debug("ActualiteService: Validating ville ID {} via VilleService", actualite.getVilleId());
            villeService.getVilleById(actualite.getVilleId())
                .orElseThrow(() -> {
                    logger.warn("ActualiteService: Ville with ID {} not found via VilleService", actualite.getVilleId());
                    return new IllegalArgumentException("Ville with ID " + actualite.getVilleId() + " not found");
                });
            logger.debug("ActualiteService: Ville validation successful via VilleService");
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