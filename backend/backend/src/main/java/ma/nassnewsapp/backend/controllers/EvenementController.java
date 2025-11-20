package ma.nassnewsapp.backend.controllers;

import ma.nassnewsapp.backend.entities.Evenement;
import ma.nassnewsapp.backend.services.EvenementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events") // Base URL for all event-related endpoints
public class EvenementController {

    private final EvenementService evenementService;

    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    @GetMapping
    public ResponseEntity<List<Evenement>> getEvents(
            @RequestParam(required = false) Integer villeId,
            @RequestParam(required = false) String categorie) {

        if (villeId != null) {
            return ResponseEntity.ok(evenementService.getEvenementsByVille(villeId));
        }
        if (categorie != null) {
            return ResponseEntity.ok(evenementService.getEvenementsByCategorie(categorie));
        }
        return ResponseEntity.ok(evenementService.getAllEvenements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEventById(@PathVariable Integer id) {
        return evenementService.getEvenementById(id)
                .map(ResponseEntity::ok) // If event exists, return 200 OK with the event
                .orElse(ResponseEntity.notFound().build()); // Otherwise, return 404 Not Found
    }

    @PostMapping
    public ResponseEntity<Evenement> createEvent(@RequestBody Evenement evenement) {
        Evenement createdEvent = evenementService.createEvenement(evenement);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED); // Return 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evenement> updateEvent(@PathVariable Integer id, @RequestBody Evenement evenementDetails) {
        return evenementService.updateEvenement(id, evenementDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        if (evenementService.deleteEvenement(id)) {
            return ResponseEntity.noContent().build(); // Return 204 No Content on success
        }
        return ResponseEntity.notFound().build(); // Return 404 if not found
    }
}