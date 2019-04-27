package Objects;

import java.util.Date;

import com.googlecode.objectify.annotation.*;

@Entity
public class Petition {

	@Id Long id;
	String mailAuteurPetition;
	String titrePetition;
	String descriptionPetition;
	@Index Date datePublicationPetition;
	
	public Petition() {};
	
	public Petition (String mailAuteurPetition, String titrePetition, String descriptionPetition, Date datePublicationPetition) {
		this.mailAuteurPetition = mailAuteurPetition;
		this.titrePetition = titrePetition;
		this.descriptionPetition = descriptionPetition;
		this.datePublicationPetition = datePublicationPetition;
	}
	
}
