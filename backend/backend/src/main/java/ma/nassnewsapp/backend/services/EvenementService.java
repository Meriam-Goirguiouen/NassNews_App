package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Evenement;
import ma.nassnewsapp.backend.repositories.EvenementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvenementService {

    private final EvenementRepository evenementRepository;

    // Constructor injection is a best practice
    public EvenementService(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
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
        // In a real app, you might want logic to ensure the ID is unique
        // or generate it from a sequence.
        return evenementRepository.ajouterEvenement(evenement);
    }

    public Optional<Evenement> updateEvenement(Integer id, Evenement evenementDetails) {
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
