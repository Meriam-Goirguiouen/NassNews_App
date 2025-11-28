package ma.nassnewsapp.backend.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "utilisateurs")
public class Utilisateur {

    @Id
    @Field("_id")
    private String idUtilisateur;

    private String nom;

    private String email;

    private String motDePasse;

    private Role role; // UTILISATEUR, ADMIN_COMMUNAL, ADMIN_SYSTEME

    private List<String> villesFavorites; // Liste d’ID de villes favorites

    public Utilisateur (){}

    public Utilisateur (String nom, String email, String motDePasse){
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // --- Getters et Setters ---
    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<String> getVillesFavorites() {
        return villesFavorites;
    }

    public void setVillesFavorites(List<String> villesFavorites) {
        this.villesFavorites = villesFavorites;
    }
}

