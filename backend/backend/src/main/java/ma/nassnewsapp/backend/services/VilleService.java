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

            // Add User-Agent header (required by Nominatim)
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "NassNewsApp/1.0");
            org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
            org.springframework.http.ResponseEntity<ReverseGeoResponse> responseEntity = 
                restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, ReverseGeoResponse.class);
            
            ReverseGeoResponse response = responseEntity.getBody();

            if (response != null && response.getAddress() != null) {
                ReverseGeoResponse.Address addr = response.getAddress();
                // Try multiple fields - Nominatim may return city in different fields
                String cityName = addr.getCity();
                if (cityName == null) cityName = addr.getTown();
                if (cityName == null) cityName = addr.getVillage();
                if (cityName == null) cityName = addr.getMunicipality();
                if (cityName == null) cityName = addr.getCityDistrict();

                if (cityName != null && !cityName.isEmpty()) {
                    Optional<Ville> existingVille = getVilleByNom(cityName);

                    if (existingVille.isPresent()) return existingVille.get();

                    // Sinon, créer et sauvegarder
                    Ville newVille = new Ville();
                    newVille.setNom(cityName);
                    newVille.setCoordonnees(latitude + "," + longitude);

                    return saveVille(newVille);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Log the error but don't throw - return empty Ville to indicate failure
        }

        return new Ville(); // Empty Ville indicates detection failed
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
            private String town;
            private String village;
            private String municipality;
            private String cityDistrict;
            
            public String getCity() { return city; }
            public void setCity(String city) { this.city = city; }
            
            public String getTown() { return town; }
            public void setTown(String town) { this.town = town; }
            
            public String getVillage() { return village; }
            public void setVillage(String village) { this.village = village; }
            
            public String getMunicipality() { return municipality; }
            public void setMunicipality(String municipality) { this.municipality = municipality; }
            
            public String getCityDistrict() { return cityDistrict; }
            public void setCityDistrict(String cityDistrict) { this.cityDistrict = cityDistrict; }
        }
    }
}
