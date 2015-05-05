/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DPCCore;

import DPCCore.messages.Destination;
import DPCCore.messages.*;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

/**
 * @author AMulroney
 */
public class DPCInstance {//  implements Runnable {

    

    public static DPCServer inBox;
    public Object outBox;
    public Object writeOut;
    public static List<DPCChatGroup> groups;
    public static List<StatusOfChatGroup> status;
    static String Nick = "";
    private static DPCMasterChatList masterChatList;
    public static String masterChatServerIP;
    public static int masterChatServerPort;
    private int State = 0;

    public DPCInstance(String publicIP, int port) {
        try {
            inBox = new DPCServer(publicIP, port);
            inBox.start();
        } catch (Exception e) {
        }
        outBox = null;
        groups = new ArrayList<>();
        status = new ArrayList<>();
        AddTestChatGroupStatus();
    }

    public DPCInstance(int port) {
        try {
            inBox = new DPCServer(InetAddress.getLocalHost().getHostAddress(), port);
            inBox.start();
        } catch (Exception e) {
        }
        outBox = null;
        groups = new ArrayList<>();
        status = new ArrayList<>();
        AddTestChatGroupStatus();
    }

    public DPCInstance(String privateIP, int port, String nick) {
        try {
            inBox = new DPCServer(privateIP, port);
            inBox.start();
        } catch (Exception e) {
        }
        outBox = null;
        groups = new ArrayList<>();
        status = new ArrayList<>();
        Nick = nick;
	AddTestChatGroupStatus();
    }

    public DPCInstance(int port, String nick) {
        try {
            inBox = new DPCServer(InetAddress.getLocalHost().getHostAddress(), port);
            inBox.start();
        } catch (Exception e) {
        }
        outBox = null;
        groups = new ArrayList<>();
        status = new ArrayList<>();
        Nick = nick;
        if (Nick != "James Connally")
            AddTestChatGroupStatus();
    }

    public void setPublicIO(java.io.OutputStream out) {
        this.writeOut = out;
        try {
            inBox.setPublicIO(out);	    
        } catch (Exception e) {
        }

    }

    public void setPublicIO(events.MessageReceived evt) {
	inBox.setPublicIO(evt);
    }
    public void setMasterChatServer(String ip, int port) {
	masterChatServerIP=ip;
        masterChatServerPort=port;
    }
    public static Destination getMasterChatServer()
    {
        String ip = "";
        int port = 0;
        
        if ("".equals(masterChatServerIP))
            ip = inBox.getIP();
        if (masterChatServerPort==0)
            port = DPCConstants.MASTER_SERVER_PORT;
        
        return new Destination(ip,"",port,"");
    }
    
    
    
    //public void addPublicIO(java.io.OutputStream out, String sNick) {
	//inBox.addPublicIO(out, sNick);	
    //}

    public String getNick() {
        return Nick;
    }
    public void setNick(String sN) {
	this.Nick = sN;
    }

    public static List<Origin> AllContacts() {
        List<Origin> c = new ArrayList<>();
        StatusOfChatGroup cg = null; //status.get(0);
        for (StatusOfChatGroup s : status)
            for (Origin o : s.Contacts)
                c.add(o);
        return c;
    }

    public static boolean isContact(Origin o) {
        if (AllContacts().contains(o))
            return true;
        else
            return false;
    }

    public static boolean isGroup(String g) {
         for (StatusOfChatGroup s : status)
            if (s.ThreadID.equalsIgnoreCase(g))
                return true;
         return false;
    }
    
    public static StatusOfChatGroup getGroup(String g) {
        for (Iterator<StatusOfChatGroup> it = status.iterator(); it.hasNext();) {
            StatusOfChatGroup s = it.next();
            if (s.ThreadID.equalsIgnoreCase(g))
                return s;
        }
         return null;
    }
    
    public void AddTestChatGroup() {
        DPCChatGroup d = new DPCChatGroup("HAJ123", "Haj To Utopia", "How The Ghadar Movement Charted Global Radicalism and attempted to Overthrow the British Empire. BLAH BLAH BLAH Green Turbans BLAH BLAH BLAH Irish Women in Sand Francisco in saris.... Roger Casement... Free Khalistan!", true);
        d.add(new Destination(inBox.getIP(), "", 1964, d.ThreadID));
        d.add(new Destination(inBox.getIP(), "", 1871, d.ThreadID));
        groups.add(d);
        //roups.Add();


    }

    public void AddTestChatGroupStatus() {
        StatusOfChatGroup g = new StatusOfChatGroup("HAJ123", "Haj To Utopia", "How The Ghadar Movement Charted Global Radicalism and attempted to Overthrow the British Empire. BLAH BLAH BLAH Green Turbans BLAH BLAH BLAH Irish Women in San Francisco in saris.... Roger Casement... Free Khalistan!", true);
        g.add(new Origin(inBox.getIP(), "", 1964, "Wonder Man", ""));
        g.add(new Origin(inBox.getIP(), "", 1871, "Octavius Catto", ""));
        status.add(g);
        //roups.Add();

    }

    //Bool returns if this is an update or an add
    public static boolean Connected(StatusOfChatGroup g) {
        for (StatusOfChatGroup s : status)
            if (s.ThreadID.equals(g.ThreadID)) {
                status.remove(s);
                status.add(g);
                return false;
            }
        status.add(g);
        return true;
    }

    public static void SendMessage(Destination d, DPCGenericObject payload) {
        try (Socket clientSocket = new Socket(d.IPv4, d.Port)) {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            DPCGenericObject g = payload;
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(g);
            json = "\"" + g.getClass().getName() + "\": " + json;
            JsonElement je1 = gson.toJsonTree(g);
            JsonObject jo1 = new JsonObject();
            jo1.add(g.getClass().getSimpleName(), je1);
            DPCMessage m = new DPCMessage(d, new Origin(inBox.getIP(), "", inBox.getPort(), Nick, ""), g.getClass().getSimpleName(), jo1);
            JsonElement je2 = gson.toJsonTree(m);
            JsonObject jo2 = new JsonObject();
            jo2.add(m.getClass().getSimpleName(), je2);
            String json1 = jo2.toString();
            outToServer.writeBytes(json1);
        } catch (Exception e) {
        }
    }

    public static void SendMessage(String thread, DPCGenericObject payload) {
        //foreach ()
        StatusOfChatGroup cg = null; //status.get(0);
        for (StatusOfChatGroup s : status)
            if (s.ThreadID.equals(thread))
                cg = s;

        if (cg == null) return;
        for (Origin o : cg.Contacts) {
            SendMessage(new Destination(o.IPv4, o.IPv6, o.Port, thread), payload);
        }
        /*try (Socket clientSocket = new Socket(d.IPv4, d.Port)) {
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            

               DPCGenericObject g = payload;
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(g);
                json = "\""+g.getClass().getName()+"\": " + json;
                 JsonElement je1 = gson.toJsonTree(g);
                JsonObject jo1 = new JsonObject();
                jo1.add(g.getClass().getSimpleName(), je1);
                
                
                
                
                DPCMessage m = new DPCMessage(d,new Origin(inBox.getIP(),"",inBox.getPort(),"Wonder Man",""),g.getClass().getSimpleName(), jo1);
                JsonElement je2 = gson.toJsonTree(m);
                JsonObject jo2 = new JsonObject();
                jo2.add(m.getClass().getSimpleName(), je2);
                String json1 = jo2.toString();
                outToServer.writeBytes(json1);
        }catch(Exception e){}*/
    }

    //sends a message to all peers in the chat group
    public static void SendMessageToChatGroup(DPCChatGroup chatGroup, DPCGenericObject payload) {
        List<Destination> allContactsList = chatGroup.getContacts();

        Iterator<Destination> listIterator = allContactsList.iterator();
        while (listIterator.hasNext()) {
            Destination destination = listIterator.next();
            SendMessage(destination, payload);
        }
    }

    public static void askForMasterChatList(Destination masterServerDestination) {
        SendMessage(masterServerDestination, new GetMasterChatList());
    }

}
