package ma.nassnewsapp.backend;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EvenementRepository extends MongoRepository<Evenement, Integer> {
	List<Evenement> findByVilleId(Integer villeId);
	List<Evenement> findByTypeEvenementIgnoreCase(String typeEvenement);
}


