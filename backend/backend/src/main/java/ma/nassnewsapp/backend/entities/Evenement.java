package ma.nassnewsapp.backend.entities;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "evenements")
public class Evenement {

	@Id
	private Integer idEvenement;

	private String titre;

	private String description;

	private String lieu;

	private LocalDate dateEvenement;

	private String typeEvenement; // categorie/type

	private Integer villeId; // to support filtering by city without modeling Ville entity yet
	
    // Add this no-argument constructor (required by frameworks like Spring Data)
    public Evenement() {
    }

    // Add this constructor for easily creating objects in your code and tests
    public Evenement(Integer idEvenement, String titre, String description, String lieu, LocalDate dateEvenement, String typeEvenement, Integer villeId) {
        this.idEvenement = idEvenement;
        this.titre = titre;
        this.description = description;
        this.lieu = lieu;
        this.dateEvenement = dateEvenement;
        this.typeEvenement = typeEvenement;
        this.villeId = villeId;
    }
	public Integer getIdEvenement() {
		return idEvenement;
	}

	public void setIdEvenement(Integer idEvenement) {
		this.idEvenement = idEvenement;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLieu() {
		return lieu;
	}

	public void setLieu(String lieu) {
		this.lieu = lieu;
	}

	public LocalDate getDateEvenement() {
		return dateEvenement;
	}

	public void setDateEvenement(LocalDate dateEvenement) {
		this.dateEvenement = dateEvenement;
	}

	public String getTypeEvenement() {
		return typeEvenement;
	}

	public void setTypeEvenement(String typeEvenement) {
		this.typeEvenement = typeEvenement;
	}

	public Integer getVilleId() {
		return villeId;
	}

	public void setVilleId(Integer villeId) {
		this.villeId = villeId;
	}
}


