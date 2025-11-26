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

    // @Field("region")
    // private String region;

    @Field("coordonnees")
    private String coordonnees; // coordonnées = "latitude,longitude"

    // @Field("population")
    // private Long population; // Optionnel

    // Constructors
    public Ville(){}
    public Ville (String id, String nom, String coordonnees){
        this.id = id;
        this.nom = nom;
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
    public String getCoordonnees() {
         return coordonnees; 
    }
    public void setCoordonnees(String coordonnees) {
         this.coordonnees = coordonnees; 
    }
    // J'ai éliminé les getters & setters des attributs : region et population (car ils sont optionnels)
}
