package ma.nassnewsapp.backend.config;

import ma.nassnewsapp.backend.entities.Utilisateur;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import jakarta.annotation.PostConstruct;

@Configuration
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    public MongoConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void createIndexes() {
        // Create unique index on email field for Utilisateur collection
        IndexOperations indexOps = mongoTemplate.indexOps(Utilisateur.class);
        
        // Check if index already exists to avoid errors on restart
        try {
            Index emailIndex = new Index()
                .on("email", org.springframework.data.domain.Sort.Direction.ASC)
                .unique();
            
            // Use the non-deprecated method
            String indexName = indexOps.ensureIndex(emailIndex);
            if (indexName != null) {
                System.out.println("Unique index on 'email' field created/verified: " + indexName);
            }
        } catch (Exception e) {
            System.err.println("Error creating index on 'email' field: " + e.getMessage());
            // Continue execution - index might already exist or there might be duplicates
            // If duplicates exist, you'll need to clean them up manually in MongoDB
        }
    }
}

