/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vehicletestapp;

import fr.utbm.set.MobileCommunicator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test client application.
 * 
 * @author Christophe Dumez <christophe.dumez@utbm.fr>
 */
public class Main {

    private static final int myid = 1;
    private static final String server_host = "localhost";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        MessageHandler msg_handler = new MessageHandler();
        MobileCommunicator communicator = new MobileCommunicator(myid, server_host, msg_handler);

        BufferedReader in = null;

        try
        {
            in = new BufferedReader(new FileReader("points.xml"));
            String str;

            while ((str=in.readLine())!=null)
            {
                String splitarray[] = str.split("\t");
                String latitude = splitarray[0];
                String longitude = splitarray[1];
                String vitesse = splitarray[2];
                String temps = splitarray[3];

                Long milliTime = System.currentTimeMillis();

                communicator.sendMessage("GPS", -11, latitude + ";" + longitude + ";" + vitesse + ";" + temps + ";" + milliTime.toString());

                System.currentTimeMillis();

                Thread.sleep(1000);
            }
            communicator.join();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException e)
        {
        }
        finally
        {
            if (in!=null)
               in.close();
        }
     }
}
