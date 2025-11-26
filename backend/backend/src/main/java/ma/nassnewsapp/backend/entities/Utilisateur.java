package ma.nassnewsapp.backend.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "utilisateurs")
public class Utilisateur {

    @Id
    private Integer idUtilisateur;

    private String nom;

    private String email;

    private String motDePasse;

    private String role; // UTILISATEUR, ADMIN_COMMUNAL, ADMIN_SYSTEME

    private List<Integer> villesFavorites; // Liste d’ID de villes favorites

    // --- Getters et Setters ---
    public Integer getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Integer idUtilisateur) {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Integer> getVillesFavorites() {
        return villesFavorites;
    }

    public void setVillesFavorites(List<Integer> villesFavorites) {
        this.villesFavorites = villesFavorites;
    }
}

