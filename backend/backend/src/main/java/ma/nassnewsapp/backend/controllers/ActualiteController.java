package ma.nassnewsapp.backend.controllers;

import ma.nassnewsapp.backend.entities.Actualite;
import ma.nassnewsapp.backend.services.ActualiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Ce contrôleur expose les points de terminaison (endpoints) de l'API REST
 * pour gérer les actualités.
 */
@RestController // Annotation essentielle qui combine @Controller et @ResponseBody. Indique que les retours des méthodes seront des données (JSON) et non des vues.
@RequestMapping("/api/actualites") // Définit l'URL de base pour toutes les méthodes de ce contrôleur. Ex: http://localhost:8080/api/actualites
public class ActualiteController {

    private final ActualiteService actualiteService;

    // Spring va automatiquement injecter votre ActualiteService ici
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
            // Si un villeId est fourni, on filtre par ville
            List<Actualite> actualites = actualiteService.getActualitesByVille(villeId);
            return ResponseEntity.ok(actualites);
        }

        if (categorie != null && !categorie.isEmpty()) {
            // Si une catégorie est fournie, on filtre par catégorie
            List<Actualite> actualites = actualiteService.getActualitesByCategorie(categorie);
            return ResponseEntity.ok(actualites);
        }

        // Si aucun filtre n'est fourni, on pourrait retourner toutes les actualités
        // (Pour l'instant, on retourne une liste vide)
        List<Actualite> toutesLesActualites = actualiteService.getAllActualites(); // Vous devrez créer cette méthode dans le service et le repository
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

        // On utilise une expression lambda pour gérer les deux cas :
        // - Si l'actualité est présente (Optional.isPresent()), on retourne ResponseEntity.ok(actualite).
        // - Sinon, on retourne ResponseEntity.notFound().build().
        return actualite.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
}