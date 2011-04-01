<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home</title>
        <link rel="stylesheet" type="text/css" href="css/design.css" media="screen" />
    </head>
    <body>
        <div id="global">
            <div id="header">
                <img alt="Logo" src="Images/logoGeofences.jpg" />
            </div><!-- header -->
            <div id="navigation">
                <ul>
                    <li class="gauche"><a href="home.jsp">Home</a></li>
                    <li class="gauche">|</li>
                    <li class="gauche"><a href="index.jsp">Geofences</a></li>
                    <li class="gauche">|</li>
                    <li class="gauche"><a href="account.jsp">Account</a></li>
                    <li class="gauche">|</li>
                    <li class="gauche"><a href="help.jsp">Help</a></li>
                    <li class="gauche">|</li>
                    <li class="gauche"><a href="AuthentificationServlet?deco=yes" >Disconnect</a></li>
                </ul>
            </div><!-- navigation -->
            <div id="centre">
                <div>
                    <p>
                        Dans un monde où l’utilisation des cartes géographiques informatisées est de plus en plus demandée pour élaborer des trajets optimaux entre un point et un autre, la classification des zones à limitations particulières se fait ressentir.
                    </p>
                    <p>
                       C’est pourquoi la mise en place de zones géographiques règlementées, appelées geofences est nécessaire.
                        <br />Ces geofences sont définies par un certain nombre d’attributs, pour différents types de véhicules. On retrouve parmi ses attributs la vitesse, le poid et la hauteur maximale.<br /> De plus ces geofences sont régies par des horaires bien spécifiques.
                    </p>
                    <p>
                        Il est possibles d'appliquer cinq types d'horaire différent tels que : d'une date à une date avec heure de début et de fin ou bien un horaire périodique, par exemple tous les vendredi d'une heure de début à une heure de fin.<br />
Le but, au final est de permettre une meilleure gestion des itinéraires. En effet un chemin plus court en distance, mais qui passe par des zones 30(km/h) par exemple, peut-être plus long en temps que d'autres itinéraires, qui eux sont plus longs en distance. <br />D'autant plus que cela met en évidence des zones auxquels certains types de véhicules ne peuvent pas passer.
                    </p>
                </div>
            </div>
            <div id="copyright">
                <span>  Copyright &copy; GSEM 2010. Tous droits résérvés.</span>
            </div>
        </div>
    </body>
</html>
