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

            // --- MODIFICATIONS CI-DESSOUS ---

            // On définit les VRAIS ID des villes ici pour que ce soit propre
            String agadirId = "692667e19351808779aaabb2"; 
            // Si vous avez l'ID de Marrakech, mettez-le ici. Sinon, on peut utiliser celui d'Agadir pour le test.
            String marrakechId = "ID_DE_MARRAKECH_SI_VOUS_L_AVEZ"; // Remplacez par le vrai ID

            // On crée les objets Actualite en utilisant les vrais ID
            Actualite a1 = new Actualite("Lancement du Festival Timitar à Agadir", "La 18ème édition du festival...", new Date(), "MAP Agence", "Culture", agadirId);
            Actualite a2 = new Actualite("Inauguration du nouveau pont suspendu", "Le nouveau pont reliant le centre-ville...", new Date(), "Le Matin du Sahara", "Infrastructure", agadirId);
            Actualite a3 = new Actualite("Préparatifs pour le Marathon de Marrakech", "Les inscriptions sont ouvertes...", new Date(), "2M.ma", "Sport", marrakechId);

            // On les sauvegarde toutes en une seule fois
            actualiteRepository.saveAll(List.of(a1, a2, a3));

            System.out.println("Données de test insérées avec succès.");
        } else {
            System.out.println("La base de données contient déjà des données. L'amorçage est ignoré.");
        }
    }
}