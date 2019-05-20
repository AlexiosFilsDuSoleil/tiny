package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;

import static com.googlecode.objectify.ObjectifyService.ofy; //permet d'écrire ofy() au lieu de ObjectifyService.Ofy()
import com.googlecode.objectify.ObjectifyService;

import Objects.Petition;
import Objects.Signature;

@SuppressWarnings("serial")
public class FichePetition extends HttpServlet {
	
	static {
		ObjectifyService.register(Petition.class);
		ObjectifyService.register(Signature.class);
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            // Demande tous les 5 derniers messages triés par date décroissante
            Query q = new Query("Petition").addSort("datePublicationPetition", SortDirection.DESCENDING);
            List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withLimit(5));

            req.setAttribute("petitions", results);
            this.getServletContext().getRequestDispatcher("/WEB-INF/FichePetition.html").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}
