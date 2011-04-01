<%--
    Document   : index
    Created on : 9 apr. 2010
    Author     : KENAN CEDRIC
    This page is used to track clients' positions and display the geofences
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="fr.utbm.set.server.*" %>
<%@page import="fr.utbm.dao.*" %>
<%@page import="fr.utbm.set.domain.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" media="screen" type="text/css" title="Design" href="style.css" />
        <title>Tracking clients</title>
        <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAGEyWe3q--H87glW56a7QehSO3srO5kgKG2CoLs1S3ioinz90FBQ-Jw0yCysI-297MAJGuXX8Rthtyw"
                type="text/javascript"></script>
        <script type="text/javascript" src="mootools.js" ></script>
        <script src="labeled_marker.js" type="text/javascript"></script>
        <script src="scripts.js" type="text/javascript"></script>

    </head>
    
    <body onload="load()" >
        <div id="menu_right">
            <%= new java.util.Date()%>
        </div>

        <h3>Clients' position <span id="satelliteError"></span></h3>
        <div id="content">
          
            <div id="mapTitle">
                <img src="Images/globe.ico">
                Map
            </div>
            <div id="mapContent">
                <div id="map"></div>
            </div>
            <div>
               <span>&nbsp;Forbidden for :</span><br/>
               <table>
                    <tr>
                        <td><span >Car & Tw </span></td>
                        <td><span style="background-color: #993300 ;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
                    </tr>
                    <tr>
                        <td><span >Car  </span></td>
                        <td><span style="background-color: #CC3300 ;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
                    </tr>
                    <tr>
                        <td><span >Two wheels </span></td>
                        <td><span style="background-color: #660066 ;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
                    </tr>
                    <tr>
                        <td><span >Truck </span></td>
                        <td><span style="background-color: #0033FF ;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
                    </tr>
                    <tr>
                        <td><span >Truck & Car </span></td>
                        <td><span style="background-color: #6633FF ;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
                     </tr>
                    <tr>
                        <td><span >Truck & Two wheels</span></td>
                        <td><span style="background-color: #9933CC ;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
                    </tr>        
                    <tr>
                        <td><span >All </span> </td>
                        <td><span style="background-color: #FF0000 ;"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
                    </tr>
                </table>
                <span id="clientInfo"></span>
                <br/>
                <span id="geoFenceInfo"></span>
            </div>
            <div style="display:none;" id="geofences">
            <%
                GeofenceDAO dao = new GeofenceDAO();

                String geof = dao.getAllGeofencesString();
                out.print(geof);
            %>
            </div>
        </div>
        <div id="footer">
            <div id="footer_copyright">Copyright &copy; GSEM 2010. Tous droits résérvés.</div>
        </div>
    </body>
</html>

