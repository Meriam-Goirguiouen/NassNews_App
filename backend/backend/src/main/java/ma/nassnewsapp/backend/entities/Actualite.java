package ma.nassnewsapp.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * Représente une information locale (actualité) liée à une ville.
 * Cette classe est mappée à une collection nommée "actualites" dans MongoDB.
 */
@Document(collection = "actualites")
public class Actualite {

    /**
     * L'identifiant unique de l'actualité, généré automatiquement par MongoDB.
     */
    @Id
    private String id;

    /**
     * Le titre de l'article d'actualité.
     */
    private String titre;

    /**
     * Le contenu détaillé de l'article.
     */
    private String contenu;

    /**
     * La date à laquelle l'actualité a été publiée.
     */
    private Date datePublication;

    /**
     * La source de l'information (ex: "Journal Local", "Agence de presse").
     */
    private String source;

    /**
     * La catégorie de l'actualité (ex: "Sport", "Politique", "Culture").
     */
    private String categorie;

    /**
     * L'identifiant de la ville à laquelle cette actualité est associée.
     * Ceci est une référence à une entité Ville .
     */
    private String villeId;


    /**
     * Constructeur par défaut.
     * Nécessaire pour le framework d'accès aux données.
     */
    public Actualite() {
    }

    /**
     * Constructeur paramétré pour créer facilement une nouvelle instance d'Actualite.
     *
     * @param titre Le titre de l'actualité.
     * @param contenu Le corps de l'article.
     * @param datePublication La date de publication.
     * @param source La source de l'information.
     * @param categorie La catégorie de l'actualité.
     * @param villeId L'ID de la ville concernée.
     */
    public Actualite(String titre, String contenu, Date datePublication, String source, String categorie, String villeId) {
        this.titre = titre;
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.source = source;
        this.categorie = categorie;
        this.villeId = villeId;
    }


    // --- Getters et Setters ---
    // Ils sont essentiels pour que Spring Data MongoDB puisse lire et écrire
    // les propriétés de l'objet.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getVilleId() {
        return villeId;
    }

    public void setVilleId(String villeId) {
        this.villeId = villeId;
    }
}