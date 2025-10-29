package ma.nassnewsapp.backend;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

	private final EvenementRepository evenementRepository;

	public EvenementController(EvenementRepository evenementRepository) {
		this.evenementRepository = evenementRepository;
	}

	@GetMapping
	public List<Evenement> getAll(
			@RequestParam(value = "villeId", required = false) Integer villeId,
			@RequestParam(value = "categorie", required = false) String categorie) {
		if (villeId != null) {
			return evenementRepository.findByVilleId(villeId);
		}
		if (categorie != null && !categorie.isBlank()) {
			return evenementRepository.findByTypeEvenementIgnoreCase(categorie);
		}
		return evenementRepository.findAll();
	}

	@PostMapping
	public Evenement ajouterEvenement(@RequestBody Evenement evenement) {
		return evenementRepository.save(evenement);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Evenement> modifierEvenement(@PathVariable Integer id, @RequestBody Evenement updated) {
		return evenementRepository.findById(id)
				.map(existing -> {
					existing.setTitre(updated.getTitre());
					existing.setDescription(updated.getDescription());
					existing.setLieu(updated.getLieu());
					existing.setDateEvenement(updated.getDateEvenement());
					existing.setTypeEvenement(updated.getTypeEvenement());
					existing.setVilleId(updated.getVilleId());
					return ResponseEntity.ok(evenementRepository.save(existing));
				})
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> supprimerEvenement(@PathVariable Integer id) {
		if (!evenementRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		evenementRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}


