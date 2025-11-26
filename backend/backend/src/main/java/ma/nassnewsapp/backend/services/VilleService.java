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
    public Ville createVille(Ville ville) { return villeRepository.save(ville); }
    public Ville saveVille(Ville ville) { return villeRepository.save(ville); }
    public List<Ville> getAllVilles() { return villeRepository.findAll(); }
    public Optional<Ville> getVilleById(String id) { return villeRepository.findById(id); }
    public Optional<Ville> getVilleByNom(String nom) { return villeRepository.findByNom(nom); }
    public void deleteVille(String id) { villeRepository.deleteById(id); }

    // --------------------------------------------------------------------
    // DÉTECTION PAR IP — fallback uniquement
    // --------------------------------------------------------------------
    public Ville detectVilleByIp(String ip) {
        try {
            String url = API_URL + ip + "?access_key=" + API_KEY;
            IpstackResponse response = restTemplate.getForObject(url, IpstackResponse.class);

            if (response != null && response.getCity() != null) {
                String cityName = response.getCity();

                Optional<Ville> existingVille = getVilleByNom(cityName);
                if (existingVille.isPresent()) return existingVille.get();

                Ville newVille = new Ville();
                newVille.setNom(cityName);
                return saveVille(newVille);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Ville();
    }

    // --------------------------------------------------------------------
    // DÉTECTION PAR COORDONNÉES — méthode principale
    // --------------------------------------------------------------------
    public Ville detectVilleByCoords(double latitude, double longitude) {
        try {
            // Appel API reverse geocoding Nominatim
            String url = "https://nominatim.openstreetmap.org/reverse"
                    + "?lat=" + latitude
                    + "&lon=" + longitude
                    + "&format=json&addressdetails=1";

            ReverseGeoResponse response =
                    restTemplate.getForObject(url, ReverseGeoResponse.class);

            if (response != null &&
                response.getAddress() != null &&
                response.getAddress().getCity() != null) {

                String cityName = response.getAddress().getCity();

                Optional<Ville> existingVille = getVilleByNom(cityName);

                if (existingVille.isPresent()) return existingVille.get();

                // Sinon, créer et sauvegarder
                Ville newVille = new Ville();
                newVille.setNom(cityName);
                newVille.setCoordonnees(latitude + "," + longitude);

                return saveVille(newVille);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Ville();
    }

    // --------------------------------------------------------------------
    // CLASSES INTERNES POUR MAPPING JSON
    // --------------------------------------------------------------------

    // Réponse IPStack
    private static class IpstackResponse {
        private String city;
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }

    // Reverse geocoding (Nominatim)
    private static class ReverseGeoResponse {
        private Address address;
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

        static class Address {
            private String city;
            public String getCity() { return city; }
            public void setCity(String city) { this.city = city; }
        }
    }
}
