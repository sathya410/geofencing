<%-- 
    Author     : CEDRIC KENAN
    This page is used to request clients' last positions
    using Ajax.
--%>
<%@page import = "fr.utbm.set.server.*" %>
<%@page import = "java.io.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
        GeofencingServerRequester requester = GeofencingServerRequester.getInstance();
        if ( requester != null )
            out.print(requester.requestLastPositions("localhost", 6113));
        else
            out.print("Error;Server is not running !/");
%>