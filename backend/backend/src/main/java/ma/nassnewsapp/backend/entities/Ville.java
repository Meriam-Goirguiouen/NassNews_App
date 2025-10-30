package ma.nassnewsapp.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="villes") // Nom de la collection MongoDB
public class Ville {
    
    @Id
    private String id;

    @Field("nom")
    private String nom;

    @Field("region")
    private String region;

    @Field("coordonnees")
    private String coordonnees; // Exemple : "latitude,longitude"

    @Field("population")
    private Long population; // Optionnel

    // Constructors
    public Ville(){}
    public Ville (String id, String nom, String region, String coordonnees, Long population){
        this.id = id;
        this.nom = nom;
        this.region = region;
        this.population = population;
    }

    //Getters & Setters
    public String getId(){
        return this.id;
    }
    public void setId(String id) { 
        this.id = id; 
    }

    public String getNom() { 
        return nom; 
    }
    public void setNom(String nom) { 
        this.nom = nom; 
    }

    public String getRegion() { 
        return region;
    }
    public void setRegion(String region) {
         this.region = region; 
    }

    public String getCoordonnees() {
         return coordonnees; 
    }
    public void setCoordonnees(String coordonnees) {
         this.coordonnees = coordonnees; 
    }

    public Long getPopulation() { 
        return population; 
    }
    public void setPopulation(Long population) { 
        this.population = population; 
    }



}
