import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;

@WebServlet(
    name = "HelloAppEngine",
    urlPatterns = {"/hello"}
)
public class HelloAppEngine extends HttpServlet {

	
	
	
	
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");

    response.getWriter().print("Bienvenue sur le servlet /hello de TinyPet in Cloud'Giroffle!\r\n");

  }

	public void test() {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		//Créer l'entité de type "Personnage" ayant l'id "groknan"
		Entity personnage = new Entity("Personnage", "groknan");
		
		//Assigner des propriétés à l'entité
		personnage.setProperty("nom", "Groknan le Barbare");
		personnage.setProperty("niveau", 25);
		personnage.setProperty("vie", 100);
		
		//Créer entité de type "Arme" avec id "pourfendeur" et comme id de parent l'id du personnage
		Entity arme = new Entity ("Arme", "pourfendeur", personnage.getKey());
		arme.setProperty("nom", "Pourfendeur des faibles");
		arme.setProperty("degats", 250);
		
		
		//Enregistrer les entités dans le datastore
		datastore.put(personnage);
		datastore.put(arme);
	}

}