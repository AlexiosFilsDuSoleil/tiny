<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="java.util.List" %>


<!DOCTYPE html>

<html>
	<head>
		<title>Créer une pétition</title>
		<meta charset="utf-8" />
	</head>

	<body>
		<h1>Création de pétition !</h1>
		<form action="/CreatePetition" method="post">
			<p>
				<label>Votre adresse mail : <input type="text" name="mailAuteurPetition" /></label>
			</p>
			<p>
				<label>Titre : <input type="text" name="titrePetition" /></label>
			</p>
			<p>
				<label>Description :

				<textarea name="descriptionPetition" style="width: 200px; height: 100px;"></textarea></label>
			</p>
			<p>
				<input type="submit" />
			</p>
		</form>
	
		<h1>Les dernières pétitions :</h1>
		<p><em>(et c'est stocké dans le Datastore !)</em></p>
		<%
			List<Entity> petitions = (List<Entity>) request.getAttribute("petitions");
			for (Entity petition : petitions) {
		%>
		<p>
			<strong><%= petition.getProperty("mailAuteurPetition") %></strong> a publié :

			<%= petition.getProperty("titrePetition") %>
		</p>
		<%
			}
		%>
	</body>
</html>