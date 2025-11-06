package ma.nassnewsapp.backend.repositories;

import ma.nassnewsapp.backend.entities.Actualite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface Repository pour l'entité Actualite.
 * En étendant MongoRepository, elle hérite de toutes les opérations CRUD de base
 * (Create, Read, Update, Delete) pour la collection "actualites".
 */
@Repository
public interface ActualiteRepository extends MongoRepository<Actualite, String> {

    // Spring Data MongoDB va automatiquement implémenter cette méthode pour vous.
    // Elle trouvera toutes les actualités dont le champ "villeId" correspond
    // à la valeur passée en paramètre.
    List<Actualite> findByVilleId(String villeId);

    //exemple, pour trouver des actualités par catégorie :
    List<Actualite> findByCategorie(String categorie);

}