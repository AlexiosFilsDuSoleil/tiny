package servlets;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy; //permet d'écrire ofy() au lieu de ObjectifyService.Ofy()

import Objects.Petition;

@SuppressWarnings("serial")
public class CreatePetition extends HttpServlet {
	
	static {
		ObjectifyService.register(Petition.class);
	}
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            // Demande tous les 5 derniers messages triés par date décroissante
            Query q = new Query("Petition").addSort("datePublicationPetition", SortDirection.DESCENDING);
            List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withLimit(100));

            req.setAttribute("petitions", results);
            this.getServletContext().getRequestDispatcher("/WEB-INF/CreatePetition.html").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {	
        try {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            // Stocke la pétition publiée
         /*
            Entity petition = new Entity("Petition");

            petition.setProperty("mailAuteurPetition", req.getParameter("mailAuteurPetition"));
            petition.setProperty("titrePetition", req.getParameter("titrePetition"));
            petition.setProperty("descriptionPetition", req.getParameter("descriptionPetition"));
            petition.setProperty("datePublicationPetition", new Date());

            datastore.put(message);
         */
           
            
            
            Petition p = new Petition(	req.getParameter("mailAuteurPetition"),
            							req.getParameter("titrePetition"),
            							req.getParameter("descriptionPetition"),
            							new Date()
            						 );
            
            ofy().save().entity(p);
            
            resp.sendRedirect("/CreatePetition");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}