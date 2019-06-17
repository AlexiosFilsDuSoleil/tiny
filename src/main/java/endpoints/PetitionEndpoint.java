package endpoints;
import com.google.api.client.util.store.DataStore;
import com.google.api.server.spi.auth.common.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.*;


@Api(name = "myApi",
version = "v1",
namespace = @ApiNamespace(ownerDomain = "helloworld.example.com",
    ownerName = "helloworld.example.com",
    packagePath = ""))

public class PetitionEndpoint {
	
	//API addPetition by Hugo et Pierre
	
	//Ajouter une pétition
	@ApiMethod(name = "addPetition")
	public Entity addPetition(@Named("mailAuteurPetition") String mailAuteurPetition, @Named("titrePetition") String titrePetition, @Named("descriptionPetition") String descriptionPetition) {
			Entity petition = new Entity("Petition", "P_"+titrePetition);
			petition.setProperty("mailAuteurPetition", mailAuteurPetition);
			petition.setProperty("titrePetition", titrePetition);
			petition.setProperty("descriptionPetition", descriptionPetition);
			petition.setProperty("nbSignature", 0);
			petition.setProperty("lastIndexOfSignature", titrePetition+"V_1");
			
			List arrayListSignature = new ArrayList();
			Entity listSignature = new Entity("Vote", petition.getKey());
			listSignature.setProperty("listSignature", arrayListSignature);
			listSignature.setIndexedProperty("indiceListeVote", titrePetition+"V_1");	
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(petition);
			datastore.put(listSignature);
			return  petition;
	}

	/* Lister toutes les pétitions, remplacé par la liste des pétitions triées
	@ApiMethod(name = "listAllPetition")
	public List<Entity> listAllPetitionEntity() {
			Query q =
			    new Query("Petition");

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
			return result;
	}
*/

	//Liste des pétitions en passant un titre en paramètre
	@ApiMethod(name = "listPetition")
	public Entity listPetition(@Named("titrePetition") String titrePetition) {
		Query q = new Query("Petition")
		        .setFilter(new FilterPredicate("titrePetition", FilterOperator.EQUAL, titrePetition));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		return result;
	}


	//Idem listAllPetition mais trié par titre
	@ApiMethod(name = "listBestPetition")
	public List<Entity> listBestPetitionEntity() {
			Query q =
			    new Query("Petition")
			    	.addSort("titrePetition", SortDirection.ASCENDING);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
			return result;
	}
		
	//Ajouter un vote avec une pétition en paramètre
	// /!\ En construction /!\
	@ApiMethod(name = "addVote")
	public Entity addVote(@Named("petitionParent") String petitionParent, @Named("emailVotant") String emailVotant, @Named("nomVotant") String nomVotant) {
			
			boolean newListe = false;
			String chaine ="";
			Query reqPetition = new Query("Petition")
			        .setFilter(new FilterPredicate("titrePetition", FilterOperator.EQUAL, petitionParent));
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(reqPetition);
			Entity result = pq.asSingleEntity();
			
			Query reqVote = new Query("Vote")
			        .setFilter(new FilterPredicate("indiceListeVote", FilterOperator.EQUAL, result.getProperty("lastIndexOfSignature")));
			PreparedQuery prepareVote = datastore.prepare(reqVote);
			Entity res = prepareVote.asSingleEntity();
			ArrayList arraySign;
			if((ArrayList)res.getProperty("listSignature") != null) {
				arraySign = (ArrayList)res.getProperty("listSignature");
			}else {
				arraySign = new ArrayList();
			}
			if(arraySign.size() >= 20000) {
				newListe = true;
				Entity listSignature = new Entity("Vote", result.getKey());
				ArrayList arraySignNew = new ArrayList();
				arraySignNew.add(emailVotant);
				listSignature.setProperty("listSignature", arraySignNew);
				chaine = (String)result.getProperty("lastIndexOfSignature");
				chaine = chaine.substring(chaine.length()-1);
				
				chaine = petitionParent+"V_"+(Integer.parseInt(chaine)+1);
				listSignature.setIndexedProperty("indiceListeVote", chaine);	
				datastore.put(listSignature);
			}else {
				arraySign.add(emailVotant);
				res.setProperty("listSignature", arraySign);
				datastore.put(res);
			}
			
			Transaction transNbSign = datastore.beginTransaction();
			try {
				result.setProperty("nbSignature", (long)result.getProperty("nbSignature")+1);
				if(newListe) {
					result.setProperty("lastIndexOfSignature", chaine);
				}
				datastore.put(transNbSign,result);
				transNbSign.commit();
			}finally {
			    if (transNbSign.isActive()) {
			    	transNbSign.rollback();
			      }
		    }
			return res;
	}
	
}