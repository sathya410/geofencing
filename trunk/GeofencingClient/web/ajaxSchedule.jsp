<%-- 
    Document   : ajaxSchedule
    Created on : 12 déc. 2010, 22:02:15
    Author     : chean
--%>

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
if(request.getParameter("selectedGeofence") != null && request.getParameter("action").equals("getGeoSchedule"))
{
    String js = new String();
    int geoID = Integer.parseInt(request.getParameter("selectedGeofence"));
    GeofenceDAO dao = new GeofenceDAO();
    String[] schedule = dao.getSchedule(geoID);
    js = "setSchedule([\"" + schedule[0] + "\"";
    
    for(int i = 1; i < schedule.length; i++){
        js += ",\"" + schedule[i] + "\"";
    }
    js += "])";

%>
    <%=js%>

<%
}

if(request.getParameter("selectedGeofence") != null && request.getParameter("action").equals("setSchedule"))
{
    String[] schedule = new String[10];
    String scheduleType = request.getParameter("type");
    String geoID = request.getParameter("selectedGeofence");

    if(scheduleType.equals("1")){
        schedule = new String[2];
        schedule[1] = request.getParameter("day");
    }
    else if(scheduleType.equals("2")){
        schedule = new String[5];
        schedule[1] = request.getParameter("dayBegin");
        schedule[2] = request.getParameter("hourBegin");
        schedule[3] = request.getParameter("dayEnd");
        schedule[4] = request.getParameter("hourEnd");
    }
    else if(scheduleType.equals("3")){
        schedule = new String[4];
        schedule[1] = request.getParameter("dayBegin");
        schedule[2] = request.getParameter("hourBegin");
        schedule[3] = request.getParameter("hourEnd");
    }
    else if(scheduleType.equals("4")){
        
        schedule[1] = request.getParameter("mon");
        schedule[2] = request.getParameter("tue");
        schedule[3] = request.getParameter("wed");
        schedule[4] = request.getParameter("thu");
        schedule[5] = request.getParameter("fri");
        schedule[6] = request.getParameter("sat");
        schedule[7] = request.getParameter("sun");

        schedule[8] = request.getParameter("hourBegin");
        schedule[9] = request.getParameter("hourEnd");

    }
    schedule[0] = scheduleType;
    

    try{
            GeofenceDAO dao = new GeofenceDAO();
            if(!geoID.equals("-1")){
               dao.updateSchedule(geoID, schedule);
            }
    } catch (Exception e){
            e.printStackTrace();
        }

}
%>