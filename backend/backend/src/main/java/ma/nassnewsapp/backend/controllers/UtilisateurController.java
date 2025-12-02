package ma.nassnewsapp.backend.controllers;

import ma.nassnewsapp.backend.entities.Utilisateur;
import ma.nassnewsapp.backend.services.UtilisateurService;
import ma.nassnewsapp.backend.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; 
import java.util.Map;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController 
@RequestMapping("/api/utilisateurs") 
public class UtilisateurController {

   
    private final UtilisateurService utilisateurService;
    private final JwtService jwtService;

    // Le contrôleur dépend du service
    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService, JwtService jwtService) {
        this.utilisateurService = utilisateurService;
        this.jwtService = jwtService;
    }

    
    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable String id) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurById(id);
        // Utiliser ResponseEntity est une bonne pratique pour gérer les cas "non trouvé" (404)
        return utilisateur.map(ResponseEntity::ok) // Si trouvé, retourne 200 OK avec l'utilisateur
                        .orElse(ResponseEntity.notFound().build()); // Sinon, retourne 404 Not Found
    }

    
    @PostMapping
    public ResponseEntity<Utilisateur> createUtilisateur(@RequestBody Utilisateur utilisateur) {
        // @RequestBody indique que l'utilisateur sera dans le corps de la requête HTTP (en JSON)
        Utilisateur savedUtilisateur = utilisateurService.createUtilisateur(utilisateur);
        // C'est une bonne pratique de retourner un statut 201 Created avec l'URL de la nouvelle ressource
        return ResponseEntity.created(URI.create("/api/utilisateurs/" + savedUtilisateur.getIdUtilisateur()))
                .body(savedUtilisateur);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable String id, @RequestBody Utilisateur utilisateurDetails) {
        return utilisateurService.updateUtilisateur(id, utilisateurDetails)
                .map(ResponseEntity::ok) // Si la mise à jour a réussi, retourne 200 OK
                .orElse(ResponseEntity.notFound().build()); // Si l'utilisateur n'existait pas, retourne 404
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable String id) {
        if (utilisateurService.deleteUtilisateur(id)) {
            return ResponseEntity.noContent().build(); // Succès, retourne 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Utilisateur non trouvé, retourne 404
        }
    }
    // --- Register ---
     @PostMapping("/register")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            Utilisateur user = utilisateurService.registerUser(
                    request.getNom(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getConfirmPassword()
            );
            return ResponseEntity.ok(user);
    } catch (RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(error); // <-- JSON
    }
    }

    // --- Login ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    Utilisateur user = utilisateurService.authenticateUser(
            request.getEmail(),
            request.getPassword()
    );

    String token = jwtService.generateToken(user.getEmail());

    return ResponseEntity.ok(Map.of(
        "token", token,
        "user", user
    ));
    }

    // --- Favorite News Endpoints ---
    @PostMapping("/{userId}/favorites/news/{newsId}")
    public ResponseEntity<Map<String, Object>> addFavoriteNews(
            @PathVariable String userId,
            @PathVariable String newsId) {
        try {
            boolean success = utilisateurService.addFavoriteNews(userId, newsId);
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "News added to favorites");
                return ResponseEntity.ok(response);
            } else {
                // User not found - return 400 Bad Request instead of 404
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{userId}/favorites/news/{newsId}")
    public ResponseEntity<Map<String, Object>> removeFavoriteNews(
            @PathVariable String userId,
            @PathVariable String newsId) {
        try {
            boolean success = utilisateurService.removeFavoriteNews(userId, newsId);
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "News removed from favorites");
                return ResponseEntity.ok(response);
            } else {
                // User not found - return 400 Bad Request instead of 404
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{userId}/favorites/news")
    public ResponseEntity<List<String>> getFavoriteNews(@PathVariable String userId) {
        List<String> favorites = utilisateurService.getFavoriteNews(userId);
        if (favorites != null) {
            return ResponseEntity.ok(favorites);
        }
        // Return empty list if user not found (better than 404 for UX)
        return ResponseEntity.ok(new java.util.ArrayList<>());
    }

    // --- Favorite Events Endpoints ---
    @PostMapping("/{userId}/favorites/events/{eventId}")
    public ResponseEntity<Map<String, Object>> addFavoriteEvent(
            @PathVariable String userId,
            @PathVariable String eventId) {
        try {
            boolean success = utilisateurService.addFavoriteEvent(userId, eventId);
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Event added to favorites");
                return ResponseEntity.ok(response);
            } else {
                // User not found - return 400 Bad Request instead of 404
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{userId}/favorites/events/{eventId}")
    public ResponseEntity<Map<String, Object>> removeFavoriteEvent(
            @PathVariable String userId,
            @PathVariable String eventId) {
        try {
            boolean success = utilisateurService.removeFavoriteEvent(userId, eventId);
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Event removed from favorites");
                return ResponseEntity.ok(response);
            } else {
                // User not found - return 400 Bad Request instead of 404
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{userId}/favorites/events")
    public ResponseEntity<List<String>> getFavoriteEvents(@PathVariable String userId) {
        List<String> favorites = utilisateurService.getFavoriteEvents(userId);
        if (favorites != null) {
            return ResponseEntity.ok(favorites);
        }
        // Return empty list if user not found (better than 404 for UX)
        return ResponseEntity.ok(new java.util.ArrayList<>());
    }

    // --- Favorite Cities Endpoints ---
    @PostMapping("/{userId}/favorites/cities/{cityId}")
    public ResponseEntity<Map<String, Object>> addFavoriteCity(
            @PathVariable String userId,
            @PathVariable String cityId) {
        try {
            boolean success = utilisateurService.addFavoriteCity(userId, cityId);
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "City added to favorites");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{userId}/favorites/cities/{cityId}")
    public ResponseEntity<Map<String, Object>> removeFavoriteCity(
            @PathVariable String userId,
            @PathVariable String cityId) {
        try {
            boolean success = utilisateurService.removeFavoriteCity(userId, cityId);
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "City removed from favorites");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{userId}/favorites/cities")
    public ResponseEntity<List<String>> getFavoriteCities(@PathVariable String userId) {
        List<String> favorites = utilisateurService.getFavoriteCities(userId);
        if (favorites != null) {
            return ResponseEntity.ok(favorites);
        }
        // Return empty list if user not found (better than 404 for UX)
        return ResponseEntity.ok(new java.util.ArrayList<>());
    }

    // --- Classes internes pour les requêtes ---
    public static class SignupRequest {
        private String nom;
        private String email;
        private String password;
        private String confirmPassword;
        // getters & setters
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }

    public static class LoginRequest {
        private String email;
        private String password;
        // getters & setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}