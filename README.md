# TinyPetInCloudGiroffle
Projet Cloud - M1 MIAGE Alternance

# Les liens

Application : http://tinypetincloudgiroffle.appspot.com/#!/home


Api explorer : https://apis-explorer.appspot.com/apis-explorer/?base=https%3A%2F%2Ftinypetincloudgiroffle.appspot.com%2F_ah%2Fapi&root=https%3A%2F%2Ftinypetincloudgiroffle.appspot.com%2F_ah%2Fapi#p/

# Schéma des entités

![image of petition](https://github.com/hugofch/TinyPetInCloudGiroffle/edit/master/entitePetititon.png)

Sur cette capture on voitl'entité Petition.
Elle comprend plusieurs champs. Sa clé est son nom. Deux pétitions ne peuvent pas avoir le même nom dans le datastore.

![image of vote](https://github.com/hugofch/TinyPetInCloudGiroffle/edit/master/EntiteVote.png)

Sur cette capture on voit l'entité Vote. Cette entié nous permet de gérer les votes des utilisateurs aux pétitions. Un utilisateur ne peut voter qu'une seule fois à une même pétition.
Chaque row a une clé parent qui est une référence à l'entité Petition. Sa clé est le nom de la pétition plus un indice. Cette indice nous sert si jamais le nombre de vote excède les 20 000 signatures. Une autre row sera alors créée.


Afin d'utiliser l'application il faudra que vous soyer connecté grâce à un vos identifiants google. 
Il se peut que le bouton d'authentification situé en bas de la page ne soit pas visible. VOus devrez donc rafraichir la page à l'aide de la touche F5 de votre clavier.
