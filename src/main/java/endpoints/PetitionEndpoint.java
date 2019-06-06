package endpoints;


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
import com.google.appengine.api.datastore.Query.*;


@Api(name = "myApi",
version = "v1",
namespace = @ApiNamespace(ownerDomain = "helloworld.example.com",
    ownerName = "helloworld.example.com",
    packagePath = ""))

public class PetitionEndpoint {
	



	//API addPetition by Hugo
	
	//Ajouter une pétition
	@ApiMethod(name = "addPetition")
	public Entity addPetition(@Named("mailAuteurPetition") String mailAuteurPetition, @Named("titrePetition") String titrePetition, @Named("descriptionPetition") String descriptionPetition) {

			Entity e = new Entity("Petition", "Petition_"+titrePetition);
			e.setProperty("mailAuteurPetition", mailAuteurPetition);
			e.setProperty("titrePetition", titrePetition);
			e.setProperty("descriptionPetition", descriptionPetition);

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(e);

			return  e;
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
	public List<Entity> listPetitionEntity(@Named("titrePetition") String titrePetition) {
			Query q =
			    new Query("Petition")
			        .setFilter(new FilterPredicate("titrePetition", FilterOperator.EQUAL, titrePetition));

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
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
	public Entity addVote(@Named("titrePetition") String titrePetition, @Named("mailVotant") String mailVotant, @Named("nomVotant") String nomVotant, @Named("prenomVotant") String prenomVotant) {

			Entity e = new Entity("Vote", titrePetition);
			e.setProperty("mailVotant", mailVotant);
			e.setProperty("nomVotant", nomVotant);
			e.setProperty("prenomVotant", prenomVotant);

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(e);

			return  e;	
	}
}