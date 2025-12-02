package ma.nassnewsapp.backend.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "utilisateurs")
public class Utilisateur {

    @Id
    @com.fasterxml.jackson.annotation.JsonProperty("idUtilisateur")
    private String idUtilisateur;

    private String nom;

    private String email;

    private String motDePasse;

    private Role role; // UTILISATEUR, ADMIN_COMMUNAL, ADMIN_SYSTEME

    private List<String> villesFavorites; // Liste d'ID de villes favorites
    
    private List<String> actualitesFavorites; // Liste d'ID d'actualités favorites
    
    private List<String> evenementsFavorites; // Liste d'ID d'événements favorites

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

    public List<String> getActualitesFavorites() {
        return actualitesFavorites;
    }

    public void setActualitesFavorites(List<String> actualitesFavorites) {
        this.actualitesFavorites = actualitesFavorites;
    }

    public List<String> getEvenementsFavorites() {
        return evenementsFavorites;
    }

    public void setEvenementsFavorites(List<String> evenementsFavorites) {
        this.evenementsFavorites = evenementsFavorites;
    }
}

