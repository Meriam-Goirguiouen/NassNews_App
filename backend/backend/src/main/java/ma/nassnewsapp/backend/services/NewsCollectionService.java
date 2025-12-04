package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.dto.ArticleDto;
import ma.nassnewsapp.backend.dto.NewsApiResponseDto;
import ma.nassnewsapp.backend.entities.Actualite;
import ma.nassnewsapp.backend.repositories.ActualiteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class NewsCollectionService {

    private final RestTemplate restTemplate;
    private final ActualiteRepository actualiteRepository;

    @Value("${newsapi.key}")
    private String apiKey;

    public NewsCollectionService(RestTemplate restTemplate, ActualiteRepository actualiteRepository) {
        this.restTemplate = restTemplate;
        this.actualiteRepository = actualiteRepository;
    }

    @Scheduled(cron = "0 0 */6 * * *") // Exécute cette méthode toutes les 6 heures
    //@Scheduled(initialDelay = 10000, fixedDelay = 3600000) // 10 secondes après le démarrage, puis toutes les heures 
    public void collectNews() {
        System.out.println("--- Début de la tâche planifiée de collecte d'actualités ---");
        
        String ville = "Agadir"; // Pour l'instant, on teste avec une ville
        
        String sourcesFiables = "le360.ma,telquel.ma,medias24.com,lesiteinfo.com,jeuneafrique.com,lemonde.fr";
        
        String url = "https://newsapi.org/v2/everything?q=" + ville 
                   + "&language=fr"
                   + "&domains=" + sourcesFiables 
                   + "&apiKey=" + apiKey;

        try {
            NewsApiResponseDto response = restTemplate.getForObject(url, NewsApiResponseDto.class);

            if (response != null && response.articles != null) {
                System.out.println(response.articles.size() + " articles trouvés via l'API pour '" + ville + "'.");
                for (ArticleDto articleDto : response.articles) {
                    // On vérifie que le titre n'est pas vide et que l'article n'existe pas déjà
                    if (articleDto.title != null && !articleDto.title.isEmpty() && actualiteRepository.findByTitre(articleDto.title).isEmpty()) {
                        
                        Actualite actualite = new Actualite();
                        actualite.setTitre(articleDto.title);
                        actualite.setContenu(articleDto.description != null ? articleDto.description : "Contenu non disponible.");
                        actualite.setSource(articleDto.source.name);
                        actualite.setDatePublication(new Date()); // On met la date actuelle de la collecte
                        actualite.setCategorie("Général"); 
                        actualite.setVilleId("692667e19351808779aaabb2"); // Plus tard, on liera ça dynamiquement

                        actualiteRepository.save(actualite);
                        System.out.println("-> Article NOUVE sauvegardé : " + actualite.getTitre());
                    } else {
                        System.out.println("-> Article déjà existant ou titre vide, ignoré : " + articleDto.title);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERREUR lors de la collecte pour la ville " + ville + ": " + e.getMessage());
        }
        System.out.println("--- Fin de la tâche de collecte ---");
    }
}