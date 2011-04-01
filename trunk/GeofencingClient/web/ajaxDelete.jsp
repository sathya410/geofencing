<%-- 
    Document   : ajaxDelete
    Created on : 13 déc. 2010, 19:08:54
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
if(request.getParameter("selectedGeofence") != null && request.getParameter("action").equals("deleteGeofence"))
{
    GeofenceDAO geoDAO = new GeofenceDAO();
    geoDAO.deleteGeofence(request.getParameter("selectedGeofence"));

    String options = "<option value=\"-1\"></option>";
    Database dtb = new Database(Database.MYSQL);
        dtb.openDatabase(ConnexionProperties.DB_URL, ConnexionProperties.DB_USR, ConnexionProperties.DB_PWD);
       // List<Geofence> listGeo = new List<Geofence>();
        if(dtb != null){
            Connection db = dtb.getConnection();
            if(db != null){
                Statement isql = db.createStatement();
                if(isql != null){
                    ResultSet rs = isql.executeQuery("select * from geofence");
                    if(rs != null){
                         while(rs.next()){
                            options += "<option value=\"" + rs.getLong("geofenceID")+ "\">" + rs.getString("geofenceName")+"</option>";
                       }
                    }
                    else
                        System.out.println("error reading resultset");
                }
                else
                    System.out.println("no statement created");
            }
            else
                System.out.println("no connection");
        }

%>
    <%=options%>
<%
}
%>
