package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.Ville;
import ma.nassnewsapp.backend.repositories.VilleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ma.nassnewsapp.backend.config.EnvConfig;
import java.util.List;
import java.util.Optional;

@Service
public class VilleService {
    
    @Autowired
    private VilleRepository villeRepository;
    private final String API_KEY = EnvConfig.getIpstackApiKey();
    private final String API_URL = "http://api.ipstack.com/";
    private final RestTemplate restTemplate = new RestTemplate();

    // --- CRUD ---

     public Ville createVille(Ville ville) {
        return villeRepository.save(ville);
    }

    public List<Ville> getAllVilles() {
        return villeRepository.findAll();
    }

    public Optional<Ville> getVilleById(String id) {
        return villeRepository.findById(id);
    }

    public Optional<Ville> getVilleByNom(String nom) {
        return villeRepository.findByNom(nom);
    }

    public void deleteVille(String id) {
        villeRepository.deleteById(id);
    }

     // --- Détection automatique de la ville via IP ---
     public Ville detectVilleByIp(String ip){
        try {
            String url = API_URL + ip + "?access_key=" + API_KEY;
            IpstackResponse response = restTemplate.getForObject(url, IpstackResponse.class);
              if (response != null && response.getCity() != null)
                {
                // Cherche dans la BD si la ville existe déjà
                Optional<Ville> existingVille = getVilleByNom(response.getCity());
                if (existingVille.isPresent()) {
                    return existingVille.get();}
                }
        } catch (Exception e) {
     e.printStackTrace();
        }
        return null;
    }
     // --- Classe interne pour mapper la réponse JSON de ipstack ---
    private static class IpstackResponse {
        private String city;
        private String region_name;
        private Double latitude;
        private Double longitude;

        // Getters & Setters
        public String getCity() { 
            return city; 
        }
        public void setCity(String city) { 
            this.city = city; 
        }
        public String getRegion_name() { 
            return region_name; 
        }
        public void setRegion_name(String region_name) { 
            this.region_name = region_name; 
        }
        public Double getLatitude() { 
            return latitude; 
        }
        public void setLatitude(Double latitude) {
             this.latitude = latitude; 
            }
        public Double getLongitude() { 
            return longitude; 
        }
        public void setLongitude(Double longitude) { 
            this.longitude = longitude;
        }
    }


    
}
