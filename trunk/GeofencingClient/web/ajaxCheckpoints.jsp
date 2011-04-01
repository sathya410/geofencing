<%-- 
    Document   : ajaxCheckpoints
    Created on : 22 nov. 2010, 21:09:26
    Author     : chean
--%>
<%@page import="java.util.ArrayList"%>
<%@page import="fr.utbm.set.domain.Vehicle"%>
<%@page import="fr.utbm.set.domain.Point"%>
<%@page import="fr.utbm.set.domain.TypeVehicle"%>
<%@page import="fr.utbm.set.domain.Geofence"%>
<%@page import="java.util.List"%>
<%@page import="fr.utbm.dao.ConnexionProperties"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="fr.utbm.set.util.Database"%>
<%@page import="fr.utbm.dao.GeofenceDAO"%>
<%
if(request.getParameter("selectedGeofence") != null && request.getParameter("action").equals("getGeoPoints"))
{
    GeofenceDAO geoDAO = new GeofenceDAO();
    Geofence geo = geoDAO.selectGeofence(Integer.parseInt(request.getParameter("selectedGeofence")));
    List<Point> listGeo = geo.getPoints();
    String points = new String();

    for(int i = 0; i < listGeo.size(); i++){
        if(i == 0)
        {
            points += listGeo.get(i).getLat() + "|" + listGeo.get(i).getLon();
         }
         else{
            points += "#" + listGeo.get(i).getLat() + "|" + listGeo.get(i).getLon();
         }
    }
%>
    <%=points%>
<%
}
if(request.getParameter("selectedGeofence") != null && request.getParameter("action").equals("getGeoInfo")){
    GeofenceDAO geoDAO = new GeofenceDAO();
    String vehicles = "[";
    Geofence geo = geoDAO.selectGeofence(Integer.parseInt(request.getParameter("selectedGeofence")));
    List<TypeVehicle> vehicle = geo.getTypeVehicles();

    if(vehicle.contains(TypeVehicle.CAR)){
        vehicles += "1";
    }
    if(vehicle.contains(TypeVehicle.TWOWHEEL)){
        if(vehicles.equals("[")){
            vehicles += "2";
        }else{
            vehicles += ",2";
        }
    }
    if(vehicle.contains(TypeVehicle.TRUCK)){
        if(vehicles.equals("[")){
            vehicles += "3";
        }else{
            vehicles += ",3";
        }
    }
    vehicles += "]";
    
    String info = new String();
    info = "setGeoInfo(\"" + geo.getName() + "\", \"" + geo.getSpeedLimit() + "\", \"" + geo.getHeightMax() + "\", \"" + geo.getWeightMax() + "\", \"" + geo.getColor() + "\"," + vehicles + " )";
%>
    <%=info%>

<%
}
%>