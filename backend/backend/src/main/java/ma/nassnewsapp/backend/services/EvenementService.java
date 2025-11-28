package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Evenement;
import ma.nassnewsapp.backend.repositories.EvenementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvenementService {

    private static final Logger logger = LoggerFactory.getLogger(EvenementService.class);
    
    private final EvenementRepository evenementRepository;
    private final VilleService villeService;

    // Constructor injection is a best practice
    public EvenementService(EvenementRepository evenementRepository, VilleService villeService) {
        this.evenementRepository = evenementRepository;
        this.villeService = villeService;
        logger.info("EvenementService initialized with VilleService dependency");
    }

    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    public Optional<Evenement> getEvenementById(String id) {
        return evenementRepository.findById(id);
    }

    public List<Evenement> getEvenementsByVille(String idVille) {
        return evenementRepository.getEvenementsByVille(idVille);
    }

    public List<Evenement> getEvenementsByCategorie(String categorie) {
        return evenementRepository.getEvenementsByCategorie(categorie);
    }

    public Evenement createEvenement(Evenement evenement) {
        // Validate ville exists before creating event
        if (evenement.getVilleId() != null && !evenement.getVilleId().isEmpty()) {
            logger.debug("EvenementService: Validating ville ID {} via VilleService", evenement.getVilleId());
            villeService.getVilleById(evenement.getVilleId())
                .orElseThrow(() -> {
                    logger.warn("EvenementService: Ville with ID {} not found via VilleService", evenement.getVilleId());
                    return new IllegalArgumentException("Ville with ID " + evenement.getVilleId() + " not found");
                });
            logger.debug("EvenementService: Ville validation successful via VilleService");
        }
        
        // MongoDB will autogenerate the String ID if not provided
        // No need to manually generate IDs for String type
        
        return evenementRepository.ajouterEvenement(evenement);
    }

    public Optional<Evenement> updateEvenement(String id, Evenement evenementDetails) {
        // Validate ville exists if villeId is being updated
        if (evenementDetails.getVilleId() != null && !evenementDetails.getVilleId().isEmpty()) {
            logger.debug("EvenementService: Validating ville ID {} via VilleService for update", evenementDetails.getVilleId());
            villeService.getVilleById(evenementDetails.getVilleId())
                .orElseThrow(() -> {
                    logger.warn("EvenementService: Ville with ID {} not found via VilleService during update", evenementDetails.getVilleId());
                    return new IllegalArgumentException("Ville with ID " + evenementDetails.getVilleId() + " not found");
                });
            logger.debug("EvenementService: Ville validation successful via VilleService for update");
        }
        
        return evenementRepository.findById(id)
                .map(existingEvent -> {
                    existingEvent.setTitre(evenementDetails.getTitre());
                    existingEvent.setDescription(evenementDetails.getDescription());
                    existingEvent.setLieu(evenementDetails.getLieu());
                    existingEvent.setDateEvenement(evenementDetails.getDateEvenement());
                    existingEvent.setTypeEvenement(evenementDetails.getTypeEvenement());
                    existingEvent.setVilleId(evenementDetails.getVilleId());
                    return evenementRepository.modifierEvenement(existingEvent);
                });
    }

    public boolean deleteEvenement(String id) {
        if (evenementRepository.existsById(id)) {
            evenementRepository.supprimerEvenement(id);
            return true;
        }
        return false;
    }
}
