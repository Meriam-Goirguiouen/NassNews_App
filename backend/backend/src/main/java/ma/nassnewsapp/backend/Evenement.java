package ma.nassnewsapp.backend;

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


