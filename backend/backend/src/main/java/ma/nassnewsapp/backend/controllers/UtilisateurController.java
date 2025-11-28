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