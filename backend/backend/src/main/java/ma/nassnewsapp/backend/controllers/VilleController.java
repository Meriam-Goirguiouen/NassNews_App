package ma.nassnewsapp.backend.controllers;


import ma.nassnewsapp.backend.entities.Ville;
import ma.nassnewsapp.backend.services.VilleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/cities")
public class VilleController {

    @Autowired
    private VilleService villeService;
  @GetMapping("/")
    public String home() {
    return "Bienvenue sur NassNews API!";
    }
     // Récupérer toutes les villes
    @GetMapping
    public List<Ville> getAllVilles() {
        return villeService.getAllVilles();
    }

    // Récupérer une ville par ID
    @GetMapping("/{id}")
    public Optional<Ville> getVilleById(@PathVariable String id) {
        return villeService.getVilleById(id);
    }

    // Récupérer une ville par nom
    @GetMapping("/nom/{nom}")
    public Optional<Ville> getVilleByNom(@PathVariable String nom) {
        return villeService.getVilleByNom(nom);
    }

    // Créer une nouvelle ville
    @PostMapping
    public Ville createVille(@RequestBody Ville ville) {
        return villeService.createVille(ville);
    }

    // Supprimer une ville
    @DeleteMapping("/{id}")
    public void deleteVille(@PathVariable String id) {
        villeService.deleteVille(id);
    }

    // Détection automatique de la ville à partir de l'IP
    @GetMapping("/detect")
    public Ville detectVille(@RequestParam String ip) {
        return villeService.detectVilleByIp(ip);
    }
    
}
