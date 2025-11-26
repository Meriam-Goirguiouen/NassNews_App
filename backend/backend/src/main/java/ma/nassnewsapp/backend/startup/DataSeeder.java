package ma.nassnewsapp.backend.startup;

import ma.nassnewsapp.backend.entities.Actualite;
import ma.nassnewsapp.backend.repositories.ActualiteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component // Indique à Spring de gérer cette classe et de l'exécuter
public class DataSeeder implements CommandLineRunner {

    private final ActualiteRepository actualiteRepository;

    public DataSeeder(ActualiteRepository actualiteRepository) {
        this.actualiteRepository = actualiteRepository;
    }

    /**
     * Cette méthode sera exécutée par Spring Boot juste après le démarrage de l'application.
     */
    @Override
    public void run(String... args) throws Exception {
        // On vérifie si la collection est vide avant d'insérer pour éviter les doublons
        if (actualiteRepository.count() == 0) {
            System.out.println("Base de données vide. Insertion des données de test...");

            // On crée les mêmes données que celles que vous avez insérées manuellement
            Actualite a1 = new Actualite("Lancement du Festival Timitar à Agadir", "La 18ème édition du festival...", new Date(), "MAP Agence", "Culture", "agadir_123");
            Actualite a2 = new Actualite("Inauguration du nouveau pont suspendu", "Le nouveau pont reliant le centre-ville...", new Date(), "Le Matin du Sahara", "Infrastructure", "agadir_123");
            Actualite a3 = new Actualite("Préparatifs pour le Marathon de Marrakech", "Les inscriptions sont ouvertes...", new Date(), "2M.ma", "Sport", "marrakech_456");

            // On les sauvegarde toutes en une seule fois
            actualiteRepository.saveAll(List.of(a1, a2, a3));

            System.out.println("Données de test insérées avec succès.");
        } else {
            System.out.println("La base de données contient déjà des données. L'amorçage est ignoré.");
        }
    }
}