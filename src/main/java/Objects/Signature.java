package Objects;

import java.util.Date;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;

@Entity
public class Signature {
	
	@Id Long id;
	@Parent Key<Petition> parent;
	String userSignature;
	String commentaireSignature;
	Date dateSignature;
	
	public Signature() {};
	
	public Signature(String userSignature, String commentaireSignature, Date dateSignature, Key<Petition> parent) {
		this.userSignature = userSignature;
		this.commentaireSignature = commentaireSignature;
		this.dateSignature = dateSignature;
		this.parent = parent;
	}
}
