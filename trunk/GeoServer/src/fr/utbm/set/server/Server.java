/*
 * Author : Kenan Cedric
 * This is the geofencing Server. Its aim is to be able to get and store client's position,
 * send these positions, accept connexions requirering informations, connect to the ServerRelay
 * and send EFA or LFA message
 */
package fr.utbm.set.server;

import fr.utbm.set.MobileCommunicator;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;
import fr.utbm.set.domain.*;
import fr.utbm.dao.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private ServerSocket serversocket;
    //the last position of each client
    private Hashtable clientsPosition;
    //contains true or false of the client was already in a forbidden area
    private HashMap alreadyInForbiddenArea;
    private HashMap vehicles;
    private ArrayList<Geofence> allGeofences;
    //communication with the ServerRelay
    private MobileCommunicator communicator;
    private final int MAXCONNECTIONNUMBER = 50;
    //private final int serverId = -11;
    private static Server instance;
    private GeofenceDAO dao;

    private Server() {
        dao = new GeofenceDAO();
        serversocket = null;
        clientsPosition = new Hashtable();
        alreadyInForbiddenArea = new HashMap();
        vehicles = new HashMap();
        allGeofences = new ArrayList<Geofence>();
        communicator = null;
    }

    public HashMap getVehicles() {
        return vehicles;
    }

    /**
     * Get an instance of the Geofencing Server (singleton class)
     * @return Server
     */
    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public boolean checkEnteredForbiddenArea(Point clientP){
        boolean res = false;

        int testLat = clientP.getXInt();
        int testLong = clientP.getYInt();

        for (int i=0; i < allGeofences.size(); i++){
             if (  allGeofences.get(i).getPolygon().contains(testLat, testLong ) ){
                res = true;
                break;
             }
        }
        return res;
    }

    /**
     * Starts the Geofencing Server : binding to socket, creating polygons corresponding to geofences,
     * linking to ServerRelay (using the class GeofencingMessageHandler), waiting for connections and
     * sending datas to requester (ServerRelay ...)
     * @param portNumber the port number
     * @param relayServerHost the host of the ServerRelay
     * @param serverId server identifier
     */
    public void startServer(int portNumber, String relayServerHost, int serverId) {
        try {
            //creating the socket and binding
            serversocket = new ServerSocket(portNumber, MAXCONNECTIONNUMBER);
            //System.out.println("Server running on port " + portNumber);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }


        //getting all the known geofences
        allGeofences = dao.selectAllGeofences();

        //connecting to Server Relay
        GeofencingMessageHandler msg_handler = new GeofencingMessageHandler(this);
        communicator = new MobileCommunicator(serverId, relayServerHost, msg_handler);

        while (!communicator.isConnected()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Socket clientConnection;
     
        PrintWriter out;
        BufferedReader in;
        String inputLine;

        //waiting for client connection and data
        while (true) {
            System.out.println("Waiting for connection ... ");
            try {
                clientConnection = serversocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            try {
                out = new PrintWriter(clientConnection.getOutputStream());
                in = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            inputLine = "";
            try {
                inputLine = in.readLine();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //System.out.println("Message received from client : " + inputLine + ": at :" + new Date());
            if (inputLine.equals("GetLastPositions")) {
                sendLastPositions(out);
            } else if (inputLine.equals("shutDown")) {
                break;
            }
            try {
                out.close();
                 in.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                clientConnection.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

  }

    /**
     * Send all last positions of each users to the requester
     * @param ObjectOutputStream the requester's outputStream
     */
    public void sendLastPositions(PrintWriter out) {
           //System.out.println("in SendLastPosition");
           String allPositions = "";
            Object tmpKey = null;
            for (Enumeration e = clientsPosition.keys(); e.hasMoreElements();) {
                tmpKey = e.nextElement();
                //System.out.println("SendLastPosition, in for loop: "+tmpKey);
                Point p = new Point();
                String status="";
                if(checkEnteredForbiddenArea(p.fromString(clientsPosition.get(tmpKey).toString()))){
                    status = "EFA";
                }else {
                    status = "LFA";
                }
                allPositions += tmpKey + ";" + clientsPosition.get(tmpKey) + ";" + status + "/";
            }
            out.write(allPositions.replace("\r", "").replace("\n", "") + "\r\n");
            out.flush();
            //System.out.println("Last positions sent ... : " + allPositions);
    }

    /**
     * Get the clients' positions
     * @return Hashtable the hashTable containing the clients' positions
     */
    public Hashtable getPositionsContent() {
        return clientsPosition;
    }

    /**
     * Get the hashtable for already forbidden clients
     * @return Hashtable the hashTable containing the clients' status
     */
    public HashMap getVehicAlredyInForbArea() {
        return alreadyInForbiddenArea;
    }

    /**
     * Change the value of a forbidden area for a client
     * If true is passed, it means to client is in a forbidden area
     * if false, it is not
     * @param int the client's id
     * @param boolean forbidden status
     */
    public void changeForbidens(int k, Geofence forb) {
        alreadyInForbiddenArea.put(k, forb);
    }

    /**
     * Get the list of all geofences
     */
    public ArrayList<Geofence> getAllGeofences() {
        return allGeofences;
    }

    /**
     * Send a message to a client via the Server Relay
     * @param String the sent command
     * @param int the id of recepient
     * @param String the content of the sent message
     */
    public void sendMsgToServerRelay(String cmd, int dest, String msg) {
        communicator.sendMessage(cmd, dest, msg);
    }

    public GeofenceDAO getDao() {
        return dao;
    }
}
