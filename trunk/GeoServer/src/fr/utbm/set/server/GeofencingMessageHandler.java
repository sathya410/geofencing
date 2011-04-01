/*
 * Author : Kenan Cedric
 * This class implements the MessageCallback interface, so it is called each time the
 * Geofencing Server receives a message from the Server Relay.
 * Then according to the message type, we process it : if we receive a 'GPS' type
 * message, then we store the position of the client in the Geofencing Server. We
 * also check if the client is entering a forbidden area (using awt Polygons
 * and the method contains) : if yes, we tell the Geofencing Server
 * to send the EFA and if the client leave a forbidden zone the LFA message will be sent.
 */
package fr.utbm.set.server;

import fr.utbm.set.MessageCallback;
import fr.utbm.set.ProtocolMessage;
import fr.utbm.set.domain.Geofence;
import fr.utbm.set.domain.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

public class GeofencingMessageHandler implements MessageCallback{

    //reference to the Geofencing Server
    private Server srv;

     /**
     * Default contructor
     * @param Server the Geofencing Server
     */
    public GeofencingMessageHandler(Server srv){
        this.srv = srv;
    }

     /**
     * When a new client's position is received we store it on the Geofencing Server.
     * We also check here if the client is entering a forbidden area or not.
     * @param ProtocolMessage the received message
     */
    public void newMessage(ProtocolMessage msg) {

        //when receives a positions from a client
        //we store it on the server as a Point
        if ( msg.getMessageType().equals("GPS") ){
            HashMap alreadyForbidden = srv.getVehicAlredyInForbArea();
            Geofence alreadyForb = null;
            int sender = msg.getSenderId();

            System.out.println("Received a GPS message from " + String.valueOf(sender) + " : at : " + new Date());
            
            String[] tokens = msg.getMessage().trim().split(";");
            double latitude = Double.valueOf(tokens[0]);
            double longitude = Double.valueOf(tokens[1]);
            double vitesse = Double.valueOf(tokens[2]);
            long temps = Long.valueOf(tokens[3]);

            //the received position
            Point receivedP = new Point(latitude, longitude);

            //checking if the client was already in a forbidden area
            if (!alreadyForbidden.containsKey(sender)){
                srv.changeForbidens(sender, null);
            }
            else
                alreadyForb = (Geofence)alreadyForbidden.get(sender);

            //if the client enters or leaves a forbidden area we
            //send a notification
            Geofence enteredForb = checkEnteredForbiddenArea(receivedP, msg);
           
            if ( enteredForb != null && alreadyForb == null){
                srv.getDao().addEvent(sender, enteredForb.getIdgeofence(), "EFA", new Date());
                System.out.println("Client : " + sender +  " : ENTERED a forbidden area at :" + new Date());
                srv.sendMsgToServerRelay("EFA", sender, (new Date()).toString());
                srv.changeForbidens(sender, enteredForb);
                Logger.getLogger("performanceGEO.log").info("EFA SENT TO : " + sender + ": AT : " + new Date());
            }
            else if ( enteredForb == null && alreadyForb != null){
                srv.getDao().addEvent(sender, alreadyForb.getIdgeofence(), "LFA", new Date());
                System.out.println("Client : " + sender + " : OUT from a forbidden area at : " + new Date());
                srv.sendMsgToServerRelay("LFA", sender, (new Date()).toString());
                srv.changeForbidens(sender, null);
                Logger.getLogger("performanceGEO.log").info("LFA SENT TO : " + sender + " AT : " + new Date());
            }

            //saving int the server the received position from the client
            srv.getPositionsContent().put(sender, receivedP);
        }
        System.out.println("Received a message from "+String.valueOf(msg.getSenderId())+": "+msg.getMessageType() + ": " + msg.getMessage());
     }

     /**
     * Chek if a client represented by it's position is in a forbidden area
     * @param Point the client's position
     * @return boolean true if the client is in a forbidden area, false if not
     */
    public Geofence checkEnteredForbiddenArea(Point clientP, ProtocolMessage msg){
        ArrayList<Geofence> allGeofences = srv.getAllGeofences();

        int testLat = clientP.getXInt();
        int testLong = clientP.getYInt();

        for (int i=0; i < allGeofences.size(); i++){
             if (  allGeofences.get(i).getPolygon().contains(testLat, testLong ) ){
                System.out.println("The client : " + msg.getSenderId() + " : is EFA at time: " + new Date());
                return allGeofences.get(i);
             }
        }
        return null;
    }

    public Geofence checkEnteredForbiddenArea(Point clientP){
        ArrayList<Geofence> allGeofences = srv.getAllGeofences();

        int testLat = clientP.getXInt();
        int testLong = clientP.getYInt();

        for (int i=0; i < allGeofences.size(); i++){
             if (  allGeofences.get(i).getPolygon().contains(testLat, testLong ))
                return allGeofences.get(i);
        }
        return null;
    }
}
