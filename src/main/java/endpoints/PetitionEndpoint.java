package endpoints;
import com.google.api.client.util.store.DataStore;
import com.google.api.server.spi.auth.common.User;

import java.text.SimpleDateFormat;
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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.repackaged.com.google.type.Date;
import com.google.appengine.api.datastore.Query.*;


@Api(name = "myApi",
version = "v1",
namespace = @ApiNamespace(ownerDomain = "helloworld.example.com",
    ownerName = "helloworld.example.com",
    packagePath = ""))

public class PetitionEndpoint {
	
	//API addPetition by Hugo et Pierre
	
	// variable permettant de définir le nombre de pétition a afficher sur l'écran d'accueil
	int limitePet = 50;
	int limiteOffSet = 0;
	
	//Ajouter une pétition
	@ApiMethod(name = "addPetition")
	public Entity addPetition(@Named("mailAuteurPetition") String mailAuteurPetition, @Named("titrePetition") String titrePetition, @Named("descriptionPetition") String descriptionPetition) {
			Entity petition = new Entity("Petition", "P_"+titrePetition);
			petition.setProperty("mailAuteurPetition", mailAuteurPetition);
			petition.setProperty("titrePetition", titrePetition);
			petition.setProperty("descriptionPetition", descriptionPetition);
			petition.setProperty("nbSignature", 0);
			
			long madate = System.currentTimeMillis();
			petition.setProperty("dateCreation",madate);
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

	// list des 100 meilleures pétitions
	@ApiMethod(name = "listeBestOfPetition", path="listeBestOfPetition")
	public List<Entity> listeBestOfPetition() {
			Query q =
			    new Query("Petition")
			    .addSort("nbSignature",SortDirection.DESCENDING);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
			return result;
	}

	//liste des pétitions avec une limite
	@ApiMethod(name = "listBestPetition", path="listBestPetition")
	public List<Entity> listBestPetitionEntity() {
			Query q =
			    new Query("Petition");
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(limitePet));
			return result;
	}
	
	// Recupere tout les votes d'une pétition
	@ApiMethod(name = "VotePetition", path="VotePetition/{titrePetition}")
	public List<PetitionSignature> getVotePetition(@Named("titrePetition") String titrePetition) {
			Query q =
			    new Query("Petition")
			    .setFilter(new FilterPredicate("titrePetition", FilterOperator.EQUAL, titrePetition));
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			Entity petition = pq.asSingleEntity();
			String keyPet = (String)petition.getProperty("lastIndexOfSignature");
			String chaine = keyPet.substring(keyPet.length()-1);
			List<PetitionSignature> listeSignature = new ArrayList<PetitionSignature>();
		
			int index = (Integer.parseInt(chaine));
			if(index > 0) {
				for(int i=index; i >= 1; i--) {
					Query querySign =
						    new Query("Vote")
						    .setFilter(new FilterPredicate("indiceListeVote", FilterOperator.EQUAL, titrePetition+"V_"+i));
					PreparedQuery prepare = datastore.prepare(querySign);
					Entity signature = prepare.asSingleEntity();
					List<String> signatureListe = (ArrayList)signature.getProperty("listSignature");
					
					if(signatureListe.size() > 0) {
						for(int nb=0; nb < signatureListe.size(); nb++) {
							listeSignature.add(new PetitionSignature(signatureListe.get(nb)));					
						}
					}
				}
			}
			return listeSignature;
	}
	
	//Récupere les suivants et précédents...
	// Un peu bugué il faudrait trouver un autre système
	// MArche bien pour le suivant. Le précédent un peu moins
	@ApiMethod(name = "nextCursor", path="nextCursor/{cursor}/{key}")
	public List<Entity> getNextCursor(@Named("cursor") String cursor, @Named("key") String key) {
		Key clePetition = KeyFactory.createKey("Petition", "P_"+key);	
		Query q;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> result;
		if(cursor.equals("next")) {
			q =
			    new Query("Petition")
			    .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, clePetition));
			PreparedQuery pq = datastore.prepare(q);
			result = pq.asList(FetchOptions.Builder.withLimit(limitePet));
			if(result.size() > 0) {
				limiteOffSet +=limitePet;
			} 
		}else {
			if(limiteOffSet > 0) {			
				limiteOffSet -= limitePet;
			}
			q =
				    new Query("Petition")
				    .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.LESS_THAN, clePetition));
			PreparedQuery pq = datastore.prepare(q);
			result = pq.asList(FetchOptions.Builder.withOffset(limiteOffSet));
			if(limiteOffSet > 0) {			
				limiteOffSet -= limitePet;
			}
		}
			
			return result;
	}

		
	// Ajouter un vote
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
	
		
		//Recupere toutes les petititons crées par une personne
		@ApiMethod(name = "listMyPetition", path="listMyPetition/{mailAuteurPetition}")
		public List<Entity> listMyPetition(@Named("mailAuteurPetition") String mailAuteurPetition) {
			Query q = new Query("Petition")
			        .setFilter(new FilterPredicate("mailAuteurPetition", FilterOperator.EQUAL, mailAuteurPetition));

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
			return result;
		}
		
		//Liste de toutes les pétitions signé par un user
		@ApiMethod(name = "listVotePetition", path="listVotePetition/{listSignature}")
		public List<Entity> listVotePetition(@Named("listSignature") String listSignature) {
			Query q = new Query("Vote")
			        .setFilter(new FilterPredicate("listSignature", FilterOperator.EQUAL, listSignature));
			
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> res = pq.asList(FetchOptions.Builder.withLimit(100));
			List<Entity> result = new ArrayList<Entity>();
			for (Entity entite : res) 
			{ 
			    try {
			    	Entity e = datastore.get(entite.getParent());
			    	result.add(e);
			    } catch (EntityNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}finally {
			    	
			    }
			}
			return result;
		}
	
}
