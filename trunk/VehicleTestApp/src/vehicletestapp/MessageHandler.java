/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vehicletestapp;

import fr.utbm.set.MessageCallback;
import fr.utbm.set.ProtocolMessage;

/**
 * Network message handler class.
 *
 * @author Christophe Dumez <christophe.dumez@utbm.fr>
 */
public class MessageHandler implements MessageCallback {

     public void newMessage(ProtocolMessage msg) {

         if (msg.getMessageType().matches("EFA")){
             String[] tokens = msg.getMessage().trim().split(";");
            Long milliTime = Long.valueOf(tokens[1]);
            System.out.println("Delay : "+((double) (System.currentTimeMillis()-milliTime)/1000)+" seconds.");
         }

        System.out.println("Received a message from "+String.valueOf(msg.getSenderId())+": "+msg.getMessageType() + ": "+msg.getMessage());
     }
}
