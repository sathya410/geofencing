/*
 * Author : Kenan Cedric
 * This aim of this class is to connect to the Geofencing Server using sockets
 * and request informations like clients' positions
 */
package fr.utbm.set.server;

import java.io.*;
import java.net.*;

public class GeofencingServerRequester {

    private Socket socket;
    private static GeofencingServerRequester instance;

    private GeofencingServerRequester() {
        socket = null;
    }

    /**
     * Get an instance of the GeofencingServerRequester (singleton class)
     * @return GeofencingServerRequester
     */
    public static GeofencingServerRequester getInstance() {
        if (instance == null) {
            instance = new GeofencingServerRequester();
        }
        return instance;
    }

    /**
     * Connects to Geofencing Server using the sockets and asking for clients'
     * positions, then returns a String containing them.
     * @param String the server where the Geofencing Server is running on
     * @param serverPort the port on which is the Geofencing Server
     * @return String The last position of each user. If an error occurs a String of
     * this type will be returned "Error; error message".
     */
    public String requestLastPositions(String serverAdr, int serverPort) {
        String res = "";
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            //Connect to Server, request and return the last positions
            socket = new Socket(serverAdr, serverPort);

            out = new PrintWriter(socket.getOutputStream());

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            boolean sendSucess = sendRequest(out, "GetLastPositions");
            if (!sendSucess) {
                res = "Error;Error while requesting the positions .../";
                return res;
            }

            //getting anwser from the server
            res = (String) in.readLine();
        } catch (UnknownHostException ex) {
            System.err.println("Error : " + ex.getStackTrace());
            res = "Error;Error : Unknownhost ! /";
        } catch (IOException e) {
            System.err.println("Error : " + e.getStackTrace());
            res = "Error;Error : IOException ! /";
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                System.err.println("Error : " + ex.getStackTrace());
            }

            return res;
        }
    }

    /**
     * Send a request to a Server using an outputSream
     * @ObjectOutputStream out the Stream in which to write
     * @String message the message to send
     * @return boolean true if the send was successful, false if not
     */
    public boolean sendRequest(PrintWriter out, String message) {
        out.write(message + "\r\n");
        out.flush();
        return true;
    }
}
