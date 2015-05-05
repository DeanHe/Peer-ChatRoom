/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MoreTesting;


//import com.cedarsoftware.util.io.*;
//import java.util.Map;
import java.io.*;
import java.net.*;
import DPCCore.*;

import java.io.IOException;

import DPCCore.messages.ConnectToChatGroup;
import DPCCore.messages.DPCGenericObject;
import DPCCore.messages.DPCMessage;
import DPCCore.messages.Ping;
import DPCCore.messages.SendMessage;
import DPCCore.messages.DPCGenericObject;
import com.google.gson.*;
/**
 *
 * @author AMulroney
 */
public class Main {
    
    
  public static void main(String [ ] args)
    {
        
        while (true){
        String command = "";
        System.out.println("--------------------------------------");
         System.out.println("More Testing: ");
                System.out.println("A) ConnectToChatGroup");
                System.out.println("B) SendMessage");
                System.out.println("C) Ping");
                try {
                    InputStreamReader isr = new InputStreamReader(System.in);
                    BufferedReader br = new BufferedReader(isr);
                    System.out.println();
                    command = br.readLine();
                    System.out.println();

                } catch (IOException e) {
                    e.printStackTrace();
                }
try{

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            try (Socket clientSocket = new Socket("localhost", 1975)) {
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            

               DPCGenericObject g = null;
                
               switch (command)
               {
                   case "A":
                   case "a":
                       g = new ConnectToChatGroup("HAJ123","");
                       ((ConnectToChatGroup)g).ThreadID = "THEADME";
                        ((ConnectToChatGroup)g).PublicKey = "PUBLICKEYME";
                       break;
                       
                   case "B":
                   case "b":
                       g = new SendMessage("I am a Message, hear me bore.");
                   
                   case "C":
                   case "c":
                       g = new Ping();
                   
                        
                   break;
                           
               }
//                ConnectToChatGroup cg = new ConnectToChatGroup();
//                cg.ThreadID = "THEADME";
//                cg.PublicKey = "PUBLICKEYME";
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(g);
                json = "\""+g.getClass().getName()+"\": " + json;
                 JsonElement je1 = gson.toJsonTree(g);
                JsonObject jo1 = new JsonObject();
                jo1.add(g.getClass().getSimpleName(), je1);
                
                
                
                
                DPCMessage m = new DPCMessage(new DPCCore.messages.Destination(),new Origin(),g.getClass().getSimpleName(), jo1);
                JsonElement je2 = gson.toJsonTree(m);
                JsonObject jo2 = new JsonObject();
                jo2.add(m.getClass().getSimpleName(), je2);
                String json1 = jo2.toString();
                outToServer.writeBytes(json1);
                
                //String json = JsonWriter.objectToJson(cg);
                
                //outToServer.writeBytes("{\"ConnectToChatGroup\": {\"ThreadID\": \"as45yu\",\"PublicKey\": \"[PublicKey]\" }}");
                
            }
}catch (Exception e1){
String s = e1.toString();
}

    }
    }
}
