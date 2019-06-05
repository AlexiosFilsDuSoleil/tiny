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


@Api(name = "myApi",
version = "v1",
namespace = @ApiNamespace(ownerDomain = "helloworld.example.com",
    ownerName = "helloworld.example.com",
    packagePath = ""))

public class PetitionEndpoint {
	


/* A SUPPRIMER, GARDE COMME EXEMPLE

	@ApiMethod(name = "listAllScore")
	public List<Entity> listAllScoreEntity() {
			Query q =
			    new Query("Score");

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
			return result;
	}


	@ApiMethod(name = "listScore")
	public List<Entity> listScoreEntity(@Named("name") String name) {
			Query q =
			    new Query("Score")
			        .setFilter(new FilterPredicate("name", FilterOperator.EQUAL, name));

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
			return result;
	}
*/
	
/*	API PERMETTANT D'AJOUTER DES SCORES
	@ApiMethod(name = "addScore")
	public Entity addScore(@Named("score") int score, @Named("name") String name) {

			Entity e = new Entity("Score", ""+name+score);
			e.setProperty("name", name);
			e.setProperty("score", score);

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(e);

			return  e;
	}
*/
	
	//API addPetition by Hugo
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
	
	@ApiMethod(name = "listAllPetition")
	public List<Entity> listAllPetitionEntity() {
			Query q =
			    new Query("Petition");

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
			return result;
	}


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
}