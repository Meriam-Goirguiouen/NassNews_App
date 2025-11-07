package ma.nassnewsapp.backend.repositories;

import ma.nassnewsapp.backend.entities.Ville;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

@Repository
public interface VilleRepository extends MongoRepository<Ville, String>{
    Optional<Ville> findByNom(String nom);
    Optional<Ville> findByRegion(String region);
    
}
