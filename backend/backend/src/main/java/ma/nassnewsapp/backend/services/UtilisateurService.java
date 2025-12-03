package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Role;
import ma.nassnewsapp.backend.entities.Utilisateur;
import ma.nassnewsapp.backend.entities.Ville;
import ma.nassnewsapp.backend.repositories.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);

    private final UtilisateurRepository utilisateurRepository;
    private final VilleService villeService;
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository, VilleService villeService, MongoTemplate mongoTemplate) {
        this.utilisateurRepository = utilisateurRepository;
        this.villeService = villeService;
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = new BCryptPasswordEncoder();
        logger.info("UtilisateurService initialized");
    }

    // ----------------------------------------------------------------------
    // GET ALL
    // ----------------------------------------------------------------------
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // ----------------------------------------------------------------------
    // GET BY ID
    // ----------------------------------------------------------------------
    public Optional<Utilisateur> getUtilisateurById(String id) {
        return utilisateurRepository.findById(id);
    }

    // ----------------------------------------------------------------------
    // GET BY EMAIL
    // ----------------------------------------------------------------------
    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findFirstByEmail(email);
    }

    // ----------------------------------------------------------------------
    // CREATE USER (WITHOUT SECURITY)
    // ----------------------------------------------------------------------
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {

        // Validation villes favorites
        if (utilisateur.getVillesFavorites() != null && !utilisateur.getVillesFavorites().isEmpty()) {
            for (String villeIdInt : utilisateur.getVillesFavorites()) {
                villeService.getVilleById(String.valueOf(villeIdInt))
                    .orElseThrow(() -> new IllegalArgumentException("Ville with ID " + villeIdInt + " not found"));
            }
        }

        // Hash the password before saving if it's provided and not already hashed
        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isEmpty()) {
            String password = utilisateur.getMotDePasse();
            // Check if password is already hashed (BCrypt hashes start with $2a$, $2b$, or $2y$)
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                // Password is plain text, hash it
                String hashedPassword = passwordEncoder.encode(password);
                utilisateur.setMotDePasse(hashedPassword);
                logger.debug("Password hashed before creating user: {}", utilisateur.getEmail());
            }
        }

        return utilisateurRepository.save(utilisateur);
    }

    // ----------------------------------------------------------------------
    // UPDATE USER
    // ----------------------------------------------------------------------
    public Optional<Utilisateur> updateUtilisateur(String id, Utilisateur utilisateurDetails) {

        if (utilisateurDetails.getVillesFavorites() != null && !utilisateurDetails.getVillesFavorites().isEmpty()) {
            for (String villeIdInt : utilisateurDetails.getVillesFavorites()) {
                villeService.getVilleById(String.valueOf(villeIdInt))
                    .orElseThrow(() -> new IllegalArgumentException("Ville with ID " + villeIdInt + " not found"));
            }
        }

        // Check if user exists
        if (!utilisateurRepository.existsById(id)) {
            return Optional.empty();
        }
        
        // Build update object with all fields that need to be updated
        Update update = new Update();
        
        if (utilisateurDetails.getNom() != null) {
            update.set("nom", utilisateurDetails.getNom());
        }
        if (utilisateurDetails.getEmail() != null) {
            update.set("email", utilisateurDetails.getEmail());
        }
        if (utilisateurDetails.getRole() != null) {
            update.set("role", utilisateurDetails.getRole());
        }
        if (utilisateurDetails.getMotDePasse() != null && !utilisateurDetails.getMotDePasse().isEmpty()) {
            // Hash the password before updating
            String hashedPassword = passwordEncoder.encode(utilisateurDetails.getMotDePasse());
            update.set("motDePasse", hashedPassword);
            logger.debug("Password hashed before update for user ID: {}", id);
        }
        if (utilisateurDetails.getVillesFavorites() != null) {
            update.set("villesFavorites", utilisateurDetails.getVillesFavorites());
        }
        if (utilisateurDetails.getVilleAssociee() != null) {
            // Validate city exists if villeAssociee is being updated
            villeService.getVilleById(utilisateurDetails.getVilleAssociee())
                .orElseThrow(() -> new IllegalArgumentException("City with ID " + utilisateurDetails.getVilleAssociee() + " not found"));
            update.set("villeAssociee", utilisateurDetails.getVilleAssociee());
        }
        
        // Use MongoTemplate to explicitly update the existing document
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.updateFirst(query, update, Utilisateur.class);
        
        // Fetch and return the updated document
        return utilisateurRepository.findById(id);
    }

    // ----------------------------------------------------------------------
    // DELETE USER
    // ----------------------------------------------------------------------
    public boolean deleteUtilisateur(String id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ----------------------------------------------------------------------
    // REGISTER (NO SECURITY)
    // ----------------------------------------------------------------------
    public Utilisateur registerUser(String nom, String email, String password, String confirmPassword) {

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords don't match!");
        }

        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists!");
        }

        // Hash the password before storing
        String hashedPassword = passwordEncoder.encode(password);
        logger.debug("Password hashed successfully for user: {}", email);

        Utilisateur user = new Utilisateur(nom, email, hashedPassword);
        user.setRole(Role.USER);

        return utilisateurRepository.save(user);
    }
    // ----------------------------------------------------------------------
    // LOGIN (NO SECURITY)
    // ----------------------------------------------------------------------
    public Utilisateur authenticateUser(String email, String password) {

        Optional<Utilisateur> optionalUser = utilisateurRepository.findFirstByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found!");
        }

        Utilisateur user = optionalUser.get();
        String storedPassword = user.getMotDePasse();

        // Check if password is already hashed (BCrypt hashes start with $2a$, $2b$, or $2y$)
        // If not hashed (legacy plain text), compare directly for backward compatibility
        // Otherwise, use BCrypt to verify
        boolean passwordMatches;
        if (storedPassword != null && (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$"))) {
            // Password is hashed, use BCrypt to verify
            passwordMatches = passwordEncoder.matches(password, storedPassword);
        } else {
            // Legacy plain text password (for backward compatibility with existing users)
            // Compare directly, but log a warning
            logger.warn("User {} has plain text password. Consider migrating to hashed password.", email);
            passwordMatches = password.equals(storedPassword);
            
            // Optionally, automatically upgrade to hashed password on successful login
            if (passwordMatches) {
                String hashedPassword = passwordEncoder.encode(password);
                Query query = new Query(Criteria.where("_id").is(user.getIdUtilisateur()));
                Update update = new Update().set("motDePasse", hashedPassword);
                mongoTemplate.updateFirst(query, update, Utilisateur.class);
                logger.info("Upgraded plain text password to hashed password for user: {}", email);
            }
        }

        if (!passwordMatches) {
            throw new IllegalArgumentException("Password incorrect!");
        }

        return user;
    }

    // ----------------------------------------------------------------------
    // FAVORITE NEWS OPERATIONS
    // ----------------------------------------------------------------------
    public boolean addFavoriteNews(String userId, String newsId) {
        logger.info("Adding favorite news - User ID: {}, News ID: {}", userId, newsId);
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {} using findById", userId);
            // Log all users to help debug
            List<Utilisateur> users = utilisateurRepository.findAll();
            logger.warn("Total users in database: {}", users.size());
            for (Utilisateur u : users) {
                logger.warn("User in DB - _id: {}, idUtilisateur: {}, email: {}", 
                    u.getIdUtilisateur(), u.getIdUtilisateur(), u.getEmail());
                // Check if any user's ID matches (case-insensitive)
                if (u.getIdUtilisateur() != null && u.getIdUtilisateur().equals(userId)) {
                    logger.info("Found matching user by direct comparison");
                    userOpt = Optional.of(u);
                    break;
                }
            }
            if (userOpt.isEmpty()) {
                logger.error("User with ID {} does not exist in database", userId);
                return false;
            }
        }
        
        Utilisateur user = userOpt.get();
        logger.debug("User before update - ID: {}, Email: {}, Favorites: {}", 
            user.getIdUtilisateur(), user.getEmail(), user.getActualitesFavorites());
        
        // Create a new list to ensure MongoDB detects the change
        List<String> favoritesList = user.getActualitesFavorites() != null 
            ? new java.util.ArrayList<>(user.getActualitesFavorites()) 
            : new java.util.ArrayList<>();
        
        if (!favoritesList.contains(newsId)) {
            favoritesList.add(newsId);
            logger.debug("Added news {} to list. List now contains: {}", newsId, favoritesList);
            
            // Use MongoTemplate to explicitly update the existing document
            Query query = new Query(Criteria.where("_id").is(userId));
            Update update = new Update().set("actualitesFavorites", favoritesList);
            
            // Use findAndModify to update and return the updated document
            Utilisateur updatedUser = mongoTemplate.findAndModify(query, update, Utilisateur.class);
            
            if (updatedUser != null) {
                logger.info("User updated successfully. User ID: {}, Email: {}, Favorites: {}", 
                    updatedUser.getIdUtilisateur(), updatedUser.getEmail(), favoritesList);
            } else {
                logger.error("CRITICAL: Update operation returned null for user ID: {}", userId);
                return false;
            }
            
            // Verify the update worked by reading back from DB
            Optional<Utilisateur> verifyUser = utilisateurRepository.findById(userId);
            if (verifyUser.isPresent()) {
                List<String> dbFavorites = verifyUser.get().getActualitesFavorites();
                logger.info("Verification - User in DB after update has favorites: {}", dbFavorites);
                if (dbFavorites == null || !dbFavorites.contains(newsId)) {
                    logger.error("CRITICAL: Update verification failed! News ID not found in DB after update.");
                }
            } else {
                logger.error("CRITICAL: User not found after update!");
            }
        } else {
            logger.debug("News {} already in favorites for user {}", newsId, userId);
        }
        
        return true;
    }

    public boolean removeFavoriteNews(String userId, String newsId) {
        logger.info("Removing favorite news - User ID: {}, News ID: {}", userId, newsId);
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {} when removing favorite news", userId);
            return false;
        }
        
        Utilisateur user = userOpt.get();
        // Ensure the ID is preserved (critical for update vs insert)
        if (user.getIdUtilisateur() == null || user.getIdUtilisateur().isEmpty()) {
            logger.error("CRITICAL: User ID is null or empty! Setting from userId parameter: {}", userId);
            user.setIdUtilisateur(userId);
        }
        
        if (user.getActualitesFavorites() != null && user.getActualitesFavorites().contains(newsId)) {
            // Create a new list without the newsId
            List<String> favoritesList = new java.util.ArrayList<>(user.getActualitesFavorites());
            favoritesList.remove(newsId);
            
            // Use MongoTemplate to explicitly update the existing document
            Query query = new Query(Criteria.where("_id").is(userId));
            Update update = new Update().set("actualitesFavorites", favoritesList);
            
            mongoTemplate.findAndModify(query, update, Utilisateur.class);
            logger.info("Successfully removed news {} from favorites for user {}", newsId, userId);
        }
        
        return true;
    }

    public List<String> getFavoriteNews(String userId) {
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {} when getting favorite news", userId);
            return null;
        }
        
        Utilisateur user = userOpt.get();
        return user.getActualitesFavorites() != null ? user.getActualitesFavorites() : new java.util.ArrayList<>();
    }

    // ----------------------------------------------------------------------
    // FAVORITE EVENTS OPERATIONS
    // ----------------------------------------------------------------------
    public boolean addFavoriteEvent(String userId, String eventId) {
        logger.info("Adding favorite event - User ID: {}, Event ID: {}", userId, eventId);
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {} when adding favorite event", userId);
            return false;
        }
        
        Utilisateur user = userOpt.get();
        logger.debug("User before update - ID: {}, Email: {}, Event Favorites: {}", 
            user.getIdUtilisateur(), user.getEmail(), user.getEvenementsFavorites());
        
        // Ensure the ID is preserved (critical for update vs insert)
        if (user.getIdUtilisateur() == null || user.getIdUtilisateur().isEmpty()) {
            logger.error("CRITICAL: User ID is null or empty! Setting from userId parameter: {}", userId);
            user.setIdUtilisateur(userId);
        }
        
        // Create a new list to ensure MongoDB detects the change
        List<String> favoritesList = user.getEvenementsFavorites() != null 
            ? new java.util.ArrayList<>(user.getEvenementsFavorites()) 
            : new java.util.ArrayList<>();
        
        if (!favoritesList.contains(eventId)) {
            favoritesList.add(eventId);
            logger.debug("Added event {} to list. List now contains: {}", eventId, favoritesList);
            
            // Use MongoTemplate to explicitly update the existing document
            Query query = new Query(Criteria.where("_id").is(userId));
            Update update = new Update().set("evenementsFavorites", favoritesList);
            
            mongoTemplate.findAndModify(query, update, Utilisateur.class);
            logger.info("User updated. Event favorites: {}", favoritesList);
        } else {
            logger.debug("Event {} already in favorites for user {}", eventId, userId);
        }
        
        return true;
    }

    public boolean removeFavoriteEvent(String userId, String eventId) {
        logger.info("Removing favorite event - User ID: {}, Event ID: {}", userId, eventId);
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {} when removing favorite event", userId);
            return false;
        }
        
        Utilisateur user = userOpt.get();
        if (user.getEvenementsFavorites() != null && user.getEvenementsFavorites().contains(eventId)) {
            // Create a new list without the eventId
            List<String> favoritesList = new java.util.ArrayList<>(user.getEvenementsFavorites());
            favoritesList.remove(eventId);
            
            // Use MongoTemplate to explicitly update the existing document
            Query query = new Query(Criteria.where("_id").is(userId));
            Update update = new Update().set("evenementsFavorites", favoritesList);
            
            mongoTemplate.findAndModify(query, update, Utilisateur.class);
            logger.info("Successfully removed event {} from favorites for user {}", eventId, userId);
        }
        
        return true;
    }

    public List<String> getFavoriteEvents(String userId) {
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {} when getting favorite events", userId);
            return null;
        }
        
        Utilisateur user = userOpt.get();
        return user.getEvenementsFavorites() != null ? user.getEvenementsFavorites() : new java.util.ArrayList<>();
    }

    // ----------------------------------------------------------------------
    // FAVORITE CITIES OPERATIONS
    // ----------------------------------------------------------------------
    public boolean addFavoriteCity(String userId, String cityId) {
        logger.info("Adding favorite city - User ID: {}, City ID: {}", userId, cityId);
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {} when adding favorite city", userId);
            return false;
        }
        
        Utilisateur user = userOpt.get();
        // Create a new list to ensure MongoDB detects the change
        List<String> favoritesList = user.getVillesFavorites() != null 
            ? new java.util.ArrayList<>(user.getVillesFavorites()) 
            : new java.util.ArrayList<>();
        
        // Filter out null values
        favoritesList.removeIf(id -> id == null || id.equals("null") || id.trim().isEmpty());
        
        if (!favoritesList.contains(cityId)) {
            favoritesList.add(cityId);
            logger.debug("Added city {} to list. List now contains: {}", cityId, favoritesList);
            
            // Use MongoTemplate to explicitly update the existing document
            Query query = new Query(Criteria.where("_id").is(userId));
            Update update = new Update().set("villesFavorites", favoritesList);
            
            mongoTemplate.findAndModify(query, update, Utilisateur.class);
            logger.info("User updated. City favorites: {}", favoritesList);
        } else {
            logger.debug("City {} already in favorites for user {}", cityId, userId);
        }
        
        return true;
    }

    public boolean removeFavoriteCity(String userId, String cityId) {
        logger.info("Removing favorite city - User ID: {}, City ID: {}", userId, cityId);
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {} when removing favorite city", userId);
            return false;
        }
        
        Utilisateur user = userOpt.get();
        if (user.getVillesFavorites() != null && user.getVillesFavorites().contains(cityId)) {
            // Create a new list without the cityId
            List<String> favoritesList = new java.util.ArrayList<>(user.getVillesFavorites());
            favoritesList.remove(cityId);
            
            // Filter out null values
            favoritesList.removeIf(id -> id == null || id.equals("null") || id.trim().isEmpty());
            
            // Use MongoTemplate to explicitly update the existing document
            Query query = new Query(Criteria.where("_id").is(userId));
            Update update = new Update().set("villesFavorites", favoritesList);
            
            mongoTemplate.findAndModify(query, update, Utilisateur.class);
            logger.info("Successfully removed city {} from favorites for user {}", cityId, userId);
        }
        
        return true;
    }

    public List<String> getFavoriteCities(String userId) {
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {} when getting favorite cities", userId);
            return null;
        }
        
        Utilisateur user = userOpt.get();
        List<String> favorites = user.getVillesFavorites() != null ? user.getVillesFavorites() : new java.util.ArrayList<>();
        
        // Filter out null values and "null" strings
        favorites = favorites.stream()
            .filter(id -> id != null && !id.equals("null") && !id.trim().isEmpty())
            .collect(Collectors.toList());
        
        return favorites;
    }

    // ----------------------------------------------------------------------
    // ADMIN COMMUNAL OPERATIONS
    // ----------------------------------------------------------------------
    public Utilisateur createAdminCommunal(String nom, String email, String password, String villeId) {
        logger.info("Creating admin communal - Name: {}, Email: {}, City ID: {}", nom, email, villeId);
        
        // Validate email doesn't exist
        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists!");
        }
        
        // Validate city exists
        Optional<Ville> villeOpt = villeService.getVilleById(villeId);
        if (villeOpt.isEmpty()) {
            throw new IllegalArgumentException("City with ID " + villeId + " not found!");
        }
        
        // Check if city already has an admin communal (optional - you can remove this if multiple admins per city are allowed)
        List<Utilisateur> existingAdmins = utilisateurRepository.findAll().stream()
            .filter(u -> u.getRole() == Role.ADMIN_COMMUNAL && villeId.equals(u.getVilleAssociee()))
            .collect(Collectors.toList());
        
        if (!existingAdmins.isEmpty()) {
            logger.warn("City {} already has an admin communal. Creating anyway.", villeId);
            // You can uncomment the line below to prevent multiple admins per city
            // throw new IllegalArgumentException("City already has an admin communal!");
        }
        
        // Hash the password
        String hashedPassword = passwordEncoder.encode(password);
        logger.debug("Password hashed successfully for admin communal: {}", email);
        
        // Create admin communal user
        Utilisateur admin = new Utilisateur(nom, email, hashedPassword);
        admin.setRole(Role.ADMIN_COMMUNAL);
        admin.setVilleAssociee(villeId);
        
        Utilisateur savedAdmin = utilisateurRepository.save(admin);
        logger.info("Admin communal created successfully - ID: {}, Email: {}, City: {}", 
            savedAdmin.getIdUtilisateur(), email, villeId);
        
        return savedAdmin;
    }

    public Optional<Utilisateur> getAdminCommunalByCity(String villeId) {
        logger.debug("Finding admin communal for city: {}", villeId);
        List<Utilisateur> admins = utilisateurRepository.findAll().stream()
            .filter(u -> u.getRole() == Role.ADMIN_COMMUNAL && villeId.equals(u.getVilleAssociee()))
            .collect(Collectors.toList());
        
        return admins.isEmpty() ? Optional.empty() : Optional.of(admins.get(0));
    }

    public List<Utilisateur> getAllAdminCommunaux() {
        logger.debug("Fetching all admin communaux");
        return utilisateurRepository.findAll().stream()
            .filter(u -> u.getRole() == Role.ADMIN_COMMUNAL)
            .collect(Collectors.toList());
    }
}