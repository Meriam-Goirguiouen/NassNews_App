package ma.nassnewsapp.backend.repositories;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import ma.nassnewsapp.backend.entities.Evenement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.lang.NonNull;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class EvenementRepositoryTest {

	@Autowired
	private EvenementRepository evenementRepository;

	@AfterEach
	void tearDown() {
		evenementRepository.deleteAll();
	}

	@Test
	void shouldFindEvenementsByVille() {
		Evenement evenementAgadir1 = buildEvenement(1, "Festival Timitar", "Concert de musique Amazighe", "Place Al Amal",
				LocalDate.of(2024, 6, 15), "Concert", 1);
		Evenement evenementAgadir2 = buildEvenement(2, "Marathon d'Agadir", "Course à pied", "Corniche",
				LocalDate.of(2024, 7, 20), "Sport", 1);
		Evenement evenementMarrakech = buildEvenement(3, "Festival du Film", "Cinéma international",
				"Palais des Congrès", LocalDate.of(2024, 11, 12), "Culture", 2);

		evenementRepository.save(evenementAgadir1);
		evenementRepository.save(evenementAgadir2);
		evenementRepository.save(evenementMarrakech);

		var resultats = evenementRepository.getEvenementsByVille(1);

		assertThat(resultats).hasSize(2);
		assertThat(resultats).extracting(Evenement::getTitre).containsExactlyInAnyOrder("Festival Timitar",
				"Marathon d'Agadir");
	}

	@Test
	void shouldFindEvenementsByCategorie() {
		Evenement evenementConcert1 = buildEvenement(10, "Festival Timitar", "Concert de musique Amazighe", "Place Al Amal",
				LocalDate.of(2024, 6, 15), "Concert", 1);
		Evenement evenementSport = buildEvenement(11, "Marathon d'Agadir", "Course à pied", "Corniche",
				LocalDate.of(2024, 7, 20), "Sport", 1);
		Evenement evenementCulture = buildEvenement(12, "Festival du Film", "Cinéma international",
				"Palais des Congrès", LocalDate.of(2024, 11, 12), "Culture", 2);
		Evenement evenementConcert2 = buildEvenement(13, "Concert Gnaoua", "Musique du monde", "Essaouira",
				LocalDate.of(2024, 9, 3), "Concert", 3);

		evenementRepository.save(evenementConcert1);
		evenementRepository.save(evenementSport);
		evenementRepository.save(evenementCulture);
		evenementRepository.save(evenementConcert2);

		var resultats = evenementRepository.getEvenementsByCategorie("Concert");

		assertThat(resultats).hasSize(2);
		assertThat(resultats).extracting(Evenement::getTitre).containsExactlyInAnyOrder("Festival Timitar",
				"Concert Gnaoua");
	}

	@Test
	void shouldAjouterEvenementAndFindIt() {
		Evenement nouvelEvenement = buildEvenement(20, "Nouvel Événement", "Description test", "Lieu Test",
				LocalDate.of(2024, 8, 1), "Test", 10);

		Evenement savedEvent = evenementRepository.ajouterEvenement(nouvelEvenement);
		int idEvenement = Objects.requireNonNull(savedEvent.getIdEvenement());

		Optional<Evenement> foundEvent = evenementRepository.findById(idEvenement);
		assertThat(foundEvent).isPresent();
		assertThat(foundEvent.get().getTitre()).isEqualTo("Nouvel Événement");
	}

	@Test
	void shouldSupprimerEvenement() {
		Evenement evenement = buildEvenement(30, "Événement à Supprimer", "Description", "Lieu",
				LocalDate.of(2024, 12, 31), "Delete", 99);
		evenementRepository.save(evenement);

		int idEvenement = Objects.requireNonNull(evenement.getIdEvenement());
		assertThat(evenementRepository.findById(idEvenement)).isPresent();

		evenementRepository.supprimerEvenement(idEvenement);

		assertThat(evenementRepository.findById(idEvenement)).isNotPresent();
	}

	private @NonNull Evenement buildEvenement(Integer id, String titre, String description, String lieu, LocalDate date,
			String typeEvenement, Integer villeId) {
		Evenement evenement = new Evenement();
		evenement.setIdEvenement(id);
		evenement.setTitre(titre);
		evenement.setDescription(description);
		evenement.setLieu(lieu);
		evenement.setDateEvenement(date);
		evenement.setTypeEvenement(typeEvenement);
		evenement.setVilleId(villeId);
		return evenement;
	}
}