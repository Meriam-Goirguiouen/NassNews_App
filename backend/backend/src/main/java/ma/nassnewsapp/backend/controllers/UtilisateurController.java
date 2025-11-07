package ma.nassnewsapp.backend.controllers;

import ma.nassnewsapp.backend.entities.Utilisateur;
import ma.nassnewsapp.backend.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController 
@RequestMapping("/api/utilisateurs") 
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // Le contrôleur dépend du service
    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    
    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Integer id) {
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
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Integer id, @RequestBody Utilisateur utilisateurDetails) {
        return utilisateurService.updateUtilisateur(id, utilisateurDetails)
                .map(ResponseEntity::ok) // Si la mise à jour a réussi, retourne 200 OK
                .orElse(ResponseEntity.notFound().build()); // Si l'utilisateur n'existait pas, retourne 404
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Integer id) {
        if (utilisateurService.deleteUtilisateur(id)) {
            return ResponseEntity.noContent().build(); // Succès, retourne 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Utilisateur non trouvé, retourne 404
        }
    }
}