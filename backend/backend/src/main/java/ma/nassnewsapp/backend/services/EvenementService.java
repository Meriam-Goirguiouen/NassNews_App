package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Evenement;
import ma.nassnewsapp.backend.repositories.EvenementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final VilleService villeService;

    // Constructor injection is a best practice
    public EvenementService(EvenementRepository evenementRepository, VilleService villeService) {
        this.evenementRepository = evenementRepository;
        this.villeService = villeService;
    }

    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    public Optional<Evenement> getEvenementById(Integer id) {
        return evenementRepository.findById(id);
    }

    public List<Evenement> getEvenementsByVille(Integer idVille) {
        return evenementRepository.getEvenementsByVille(idVille);
    }

    public List<Evenement> getEvenementsByCategorie(String categorie) {
        return evenementRepository.getEvenementsByCategorie(categorie);
    }

    public Evenement createEvenement(Evenement evenement) {
        // Validate ville exists before creating event
        if (evenement.getVilleId() != null) {
            villeService.getVilleById(String.valueOf(evenement.getVilleId()))
                .orElseThrow(() -> new IllegalArgumentException("Ville with ID " + evenement.getVilleId() + " not found"));
        }
        return evenementRepository.ajouterEvenement(evenement);
    }

    public Optional<Evenement> updateEvenement(Integer id, Evenement evenementDetails) {
        // Validate ville exists if villeId is being updated
        if (evenementDetails.getVilleId() != null) {
            villeService.getVilleById(String.valueOf(evenementDetails.getVilleId()))
                .orElseThrow(() -> new IllegalArgumentException("Ville with ID " + evenementDetails.getVilleId() + " not found"));
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

    public boolean deleteEvenement(Integer id) {
        if (evenementRepository.existsById(id)) {
            evenementRepository.supprimerEvenement(id);
            return true;
        }
        return false;
    }
}
