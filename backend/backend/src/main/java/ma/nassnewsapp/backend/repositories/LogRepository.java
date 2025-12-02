package ma.nassnewsapp.backend.repositories;

import ma.nassnewsapp.backend.entities.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends MongoRepository<LogEntry, String> {

    List<LogEntry> findByLevelOrderByTimestampDesc(String level);

    List<LogEntry> findByServiceOrderByTimestampDesc(String service);

    @Query("{ 'timestamp': { $gte: ?0, $lte: ?1 } }")
    List<LogEntry> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

    @Query("{ 'level': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } }")
    List<LogEntry> findByLevelAndTimestampBetweenOrderByTimestampDesc(String level, LocalDateTime start, LocalDateTime end);

    List<LogEntry> findAllByOrderByTimestampDesc();
}

