package ma.nassnewsapp.backend.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import ma.nassnewsapp.backend.entities.Evenement;

@Repository
public interface EvenementRepository extends MongoRepository<Evenement, Integer> {

	/**
	 * Récupère tous les événements associés à une ville spécifique.
	 * 
	 * @param idVille L'identifiant de la ville
	 * @return Liste des événements de la ville
	 */
	@Query("{ 'villeId' : ?0 }")
	List<Evenement> getEvenementsByVille(Integer idVille);

	/**
	 * Récupère tous les événements d'une catégorie spécifique.
	 * 
	 * @param categorie La catégorie/type de l'événement
	 * @return Liste des événements de la catégorie
	 */
	@Query("{ 'typeEvenement' : ?0 }")
	List<Evenement> getEvenementsByCategorie(String categorie);

	/**
	 * Ajoute un nouvel événement à la base de données.
	 * 
	 * @param evenement L'événement à ajouter
	 * @return L'événement sauvegardé avec son ID généré
	 */
	default Evenement ajouterEvenement(Evenement evenement) {
		return save(evenement);
	}

	/**
	 * Modifie un événement existant dans la base de données.
	 * 
	 * @param evenement L'événement à modifier
	 * @return L'événement mis à jour
	 */
	default Evenement modifierEvenement(Evenement evenement) {
		return save(evenement);
	}

	/**
	 * Supprime un événement de la base de données par son ID.
	 * 
	 * @param idEvenement L'identifiant de l'événement à supprimer
	 */
	default void supprimerEvenement(Integer idEvenement) {
		deleteById(idEvenement);
	}
}

