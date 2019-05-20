<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="java.util.List" %>


<!DOCTYPE html>

<html>
	<head>
		<title>Petition : XXX</title>
		<meta charset="utf-8" />
	</head>

	<body>

	<h1>Détail de la pétition ""</h1>

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