/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import fr.utbm.set.server.Server;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author wait-ch1
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String address = "";
        int port = 0;
        int serverId = 0;

        //we save the arguments
        if (args.length != 3)
        {
            System.out.println("Wrong arguments");
            System.exit(0);
        }
        {
            address = args[0];
            port = Integer.valueOf(args[1]);
            serverId = Integer.valueOf(args[2]);
        }

        FileHandler handler;
        try {
            handler = new FileHandler("performanceGEO.log", true);
            handler.setFormatter(new SimpleFormatter());
            Logger logger = Logger.getLogger("performanceGEO.log");
            logger.addHandler(handler);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }       
        Server.getInstance().startServer(port, address, serverId);
    }

}
