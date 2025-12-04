package ma.nassnewsapp.backend.controllers;

import ma.nassnewsapp.backend.entities.Actualite;
import ma.nassnewsapp.backend.services.ActualiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// --- MODIFICATION 1 : AJOUT DE L'IMPORT ---
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

/**
 * Ce contrôleur expose les points de terminaison (endpoints) de l'API REST
 * pour gérer les actualités.
 */
@RestController 
@RequestMapping("/api/actualites")
// --- MODIFICATION 2 : AJOUT DE L'ANNOTATION ---
@CrossOrigin(origins = "http://localhost:5173") // Autorise les requêtes depuis votre frontend Vue.js
public class ActualiteController {

    private final ActualiteService actualiteService;

    public ActualiteController(ActualiteService actualiteService) {
        this.actualiteService = actualiteService;
    }

    /**
     * Endpoint pour récupérer des actualités, potentiellement filtrées.
     * URL : GET /api/actualites?villeId=... ou GET /api/actualites?categorie=...
     *
     * @param villeId   (Optionnel) L'ID de la ville pour filtrer les résultats.
     * @param categorie (Optionnel) La catégorie pour filtrer les résultats.
     * @return Une liste d'actualités et un statut HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Actualite>> getActualites(
            @RequestParam(required = false) String villeId,
            @RequestParam(required = false) String categorie) {

        if (villeId != null && !villeId.isEmpty()) {
            List<Actualite> actualites = actualiteService.getActualitesByVille(villeId);
            return ResponseEntity.ok(actualites);
        }

        if (categorie != null && !categorie.isEmpty()) {
            List<Actualite> actualites = actualiteService.getActualitesByCategorie(categorie);
            return ResponseEntity.ok(actualites);
        }

        List<Actualite> toutesLesActualites = actualiteService.getAllActualites();
        return ResponseEntity.ok(toutesLesActualites);
    }

    /**
     * Endpoint pour récupérer une actualité spécifique par son ID.
     * URL : GET /api/actualites/{id}
     *
     * @param id L'ID de l'actualité à récupérer (provient de l'URL).
     * @return L'actualité trouvée avec un statut 200 OK, ou un statut 404 Not Found si elle n'existe pas.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Actualite> getActualiteById(@PathVariable String id) {
        Optional<Actualite> actualite = actualiteService.getActualiteById(id);
        
        return actualite.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint pour créer une nouvelle actualité.
     * URL : POST /api/actualites
     *
     * @param actualite L'actualité à créer (provient du corps de la requête JSON).
     * @return L'actualité créée avec un statut 201 Created.
     */
    @PostMapping
    public ResponseEntity<Actualite> createActualite(@RequestBody Actualite actualite) {
        // Note: Vous devrez aussi créer la méthode 'createActualite' dans votre ActualiteService
        try {
            Actualite createdActualite = actualiteService.createActualite(actualite);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdActualite);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}