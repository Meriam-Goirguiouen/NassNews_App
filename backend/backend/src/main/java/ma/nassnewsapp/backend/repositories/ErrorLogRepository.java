package ma.nassnewsapp.backend.repositories;

import ma.nassnewsapp.backend.entities.ErrorLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ErrorLogRepository extends MongoRepository<ErrorLog, String> {

    List<ErrorLog> findByLevel(String level);

    List<ErrorLog> findByService(String service);

    @Query("{ 'timestamp': { $gte: ?0, $lte: ?1 } }")
    List<ErrorLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("{ 'level': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } }")
    List<ErrorLog> findByLevelAndTimestampBetween(String level, LocalDateTime start, LocalDateTime end);

    List<ErrorLog> findAllByOrderByTimestampDesc();
}

