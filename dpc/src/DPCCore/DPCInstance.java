package DPCCore;

import DPCCore.messages.*;
import security.EncryptionHandler;
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import javax.crypto.SealedObject;

/**
 * DPCInstance.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * DPCInstance is the peer object. This is the core of the protocol since it creates point to point feature.
 */

public class DPCInstance {//  implements Runnable {

    //inBox(DPCServer) handles the incoming message  
    public static DPCServer inBox;
    public Object outBox;
    public Object writeOut;
    public static EncryptionHandler encryptionHandler;
    //The list of all chat groups that the peer can connect to
    public static List<DPCChatGroup> groups;
    //The list of all the groups with their group members. 
    //It is needed in order to send message to everyone in the group.
    public static List<StatusOfChatGroup> status;
    static String Nick = "";
    protected static DPCMasterChatList masterChatList;
    public static String masterChatServerIP;
    public static int masterChatServerPort;
    protected static Destination MasterChatServer;
    //initial state of the peer.
    protected static DPCConstants.State state = DPCConstants.State.NOT_CONNECTED;
    //Keeping track of the states.
    private static List<DPCStateMonitor> waiting = new ArrayList<>();
    private static events.MessageReceived eMessageWriter;
    //List of all unavailable peers.
    private static List<Origin>deadPeers =  new ArrayList<>(); 

    //List of different constructors with different input parameters
    public DPCInstance(String publicIP, int port) {
        try {
	          encryptionHandler = new EncryptionHandler();
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
	          encryptionHandler = new EncryptionHandler();
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
	          encryptionHandler = new EncryptionHandler();
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
	          encryptionHandler = new EncryptionHandler();
            inBox = new DPCServer(InetAddress.getLocalHost().getHostAddress(), port);
            inBox.start();
        } catch (Exception e) {
        }
        outBox = null;
        groups = new ArrayList<>();
        status = new ArrayList<>();
        Nick = nick;
        //here James Connally is added only for presentation purposes.
        //The protocol works without hardcoding users.
        if (Nick != "James Connally")
            AddTestChatGroupStatus();
    }

    //Setting a way to log information 
    public void setPublicIO(java.io.OutputStream out) {
        this.writeOut = out;
        try {
            inBox.setPublicIO(out);
        } catch (Exception e) {
        }

    }

    //Setting message event handler.
    public void setPublicIO(events.MessageReceived evt) {
        eMessageWriter = evt;
	inBox.setPublicIO(evt);
    }
    //Setting the master chat server that peer needs to connect to 
    //in order to get master chat list.
    public static void setMasterChatServer(String ip, int port) {
	    masterChatServerIP=ip;
        masterChatServerPort=port;
        
        if ("".equals(masterChatServerIP))
            ip = inBox.getIP();
        if (masterChatServerPort==0)
            port = DPCConstants.MASTER_SERVER_PORT;
        
        MasterChatServer  =  new Destination(ip,"",port,"");
    }

    //returns master chat server as a destination object
    public static Destination getMasterChatServer()
    {
        if (MasterChatServer==null) setMasterChatServer("",0);
        
        return MasterChatServer;
    }
    
    
    
    //public void addPublicIO(java.io.OutputStream out, String sNick) {
	//inBox.addPublicIO(out, sNick);	
    //}

    public static String getNick() {
        return Nick;
    }
    public void setNick(String sN) {
	this.Nick = sN;
    }

    //returns a list of peers that are present in the entire chat
    //from all chat groups.
    public static List<Origin> AllContacts() {
        List<Origin> c = new ArrayList<>();
        StatusOfChatGroup cg = null; //status.get(0);
        for (StatusOfChatGroup s : status)
            for (Origin o : s.Contacts)
                c.add(o);
        return c;
    }

    //verifies whether a given contact(ip and port) is present 
    //in the entire chat.
    public static boolean isContact(Origin o) {
        if (AllContacts().contains(o))
            return true;
        else
            return false;
    }

    public static Origin getContact(String nick) {
        for (StatusOfChatGroup s : status)
            for (Origin o : s.Contacts)
            {
                if (o.Nick.equals(nick))
                    return o;
            }
        return null;
    }

    //Getting the contact as Origin based on its IP and port
    public static Origin getContactByIPPort(String IP, int port) {
        
        for (StatusOfChatGroup s : status)
            for (Origin o : s.Contacts)
            {
                if (o.IPv4.equals(IP) && o.Port==port)
                    return o;
            }
    
        return null;
    }

    //Checks whether a given chat group exists in the entire chat.
    public static boolean isGroup(String g) {
         for (StatusOfChatGroup s : status)
           if (s.ThreadID.equalsIgnoreCase(g))
                return true;
         return false;
    }

    //Gets a chat groups based on its thread id.
    public static DPCChatGroup getChatGroupFromMCL(String s)
    {
        for (DPCChatGroup g : masterChatList.getChatGroups())
        {
            if (g.ThreadID.equals(s))
                return g;
            
        }
        return null;
    }

    //Gets the StatusOfChatGroup based on its thread id
    //StatusOfChatGroup has information of all peers present in the chat group
    public static StatusOfChatGroup getGroup(String g) {
        for (Iterator<StatusOfChatGroup> it = status.iterator(); it.hasNext();) {
            StatusOfChatGroup s = it.next();
            if (s.ThreadID.equalsIgnoreCase(g))
                return s;
        }
         return null;
    }

    //Updates StatusOfChatGroup with the status of a given group.
    //If the group does not exist in the list, it adds it.
    public static boolean setGroup(StatusOfChatGroup g) {
        for (Iterator<StatusOfChatGroup> it = status.iterator(); it.hasNext();) {
            StatusOfChatGroup s = it.next();
            if (s.ThreadID.equalsIgnoreCase(g.ThreadID))
            {
                status.remove(s);
                status.add(g);
                return true;
            }
        }
        status.add(g);
        return true;
        //return null;
    }

    //Makes sure that a member of chat group is in the group.
    //If it is not then it is added.
    public static boolean syncGroup(Origin o, String s) {
        StatusOfChatGroup group = getGroup(s);
        for (Origin o1 : group.Contacts)
        {
            if (o1.Nick.equals(o.Nick))
                return true;
        }
        group.add(o);
        return true;
    }
    
    //Creates a sample chat group with two peers inside for testing.
    public void AddTestChatGroup() {
        DPCChatGroup d = new DPCChatGroup("HAJ123", "Haj To Utopia", "How The Ghadar Movement Charted Global Radicalism and attempted to Overthrow the British Empire. BLAH BLAH BLAH Green Turbans BLAH BLAH BLAH Irish Women in Sand Francisco in saris.... Roger Casement... Free Khalistan!", true);
        d.add(new Destination(inBox.getIP(), "", 1964, d.ThreadID));
        d.add(new Destination(inBox.getIP(), "", 1871, d.ThreadID));
        groups.add(d);
        //roups.Add();


    }
  
    //Creates a saple status chat group for testing.
    public void AddTestChatGroupStatus() {
        StatusOfChatGroup g = new StatusOfChatGroup("HAJ123", "Haj To Utopia", "How The Ghadar Movement Charted Global Radicalism and attempted to Overthrow the British Empire. BLAH BLAH BLAH Green Turbans BLAH BLAH BLAH Irish Women in San Francisco in saris.... Roger Casement... Free Khalistan!", true);
        g.add(new Origin(inBox.getIP(), "", 1964, "Wonder Man", ""));
        g.add(new Origin(inBox.getIP(), "", 1871, "Octavius Catto", ""));
        status.add(g);
        //roups.Add();

    }

    //A peer joins a chat group that already exists.
    public static void joinChatGroup(DPCChatGroup group)
    {
        DPCCore.Destination d=new DPCCore.Destination(group.Contacts.get(0).IPv4, "", group.Contacts.get(0).Port, group.ThreadID);
        DPCChatGroup g = getChatGroupFromMCL(group.ThreadID);
        setCallback(g , "ConnectToChatGroup", new ConnectToChatGroup(group.ThreadID,""));
    }

    //The peer creates a chat group, where it is the administarator.
    //Also sends that chat group the master server.
    public static void createChatGroup(DPCChatGroup group)
    {
        group.Contacts.add(new Destination(inBox.getIP(),"",inBox.getPort(),""));
        //DPCCore.Destination d=new DPCCore.Destination(group.Contacts.get(0).IPv4, "", group.Contacts.get(0).Port, group.ThreadID);
        //DPCInstance.SendMessage(d, new ConnectToChatGroup(group.ThreadID,""));
        setCallback(MasterChatServer, "UpdateMasterChatList", new UpdateMasterChatList(group));
    }

    //The peer creates a chat group, where it is the administarator.
    //Also sends that chat group the master server.
    public static void createChatGroup(String threadID, String title, String desc)
    {
        DPCChatGroup group = new DPCChatGroup(threadID,title,desc);
        group.Contacts.add(new Destination(inBox.getIP(),"",inBox.getPort(),""));
        //DPCCore.Destination d=new DPCCore.Destination(group.Contacts.get(0).IPv4, "", group.Contacts.get(0).Port, group.ThreadID);
        setCallback(MasterChatServer, "UpdateMasterChatList", new UpdateMasterChatList(group));
        //DPCInstance.SendMessage(d, new ConnectToChatGroup(group.ThreadID,""));
        //setCallback(MasterChatServer, "UpdateMasterChatList", new UpdateMasterChatList(group.ThreadID,""));
    }

    //The peer that is an administrator sends an update version of the chat group 
    //to the master chat server.
    public static void updateChatGroup(StatusOfChatGroup g)
    {
        DPCChatGroup group = getChatGroupFromMCL(g.ThreadID);
        setCallback(MasterChatServer, "UpdateMasterChatList", new UpdateMasterChatList(group));
    }
    
    /*
     * This function is called after connect to chat group - The 
     */
    public static boolean Connected(StatusOfChatGroup g) {
        for (StatusOfChatGroup s : status)
            if (s.ThreadID.equals(g.ThreadID)) {
                status.remove(s);
                
                if (s.adminFlag())
                    g.setAdminFlag(true);
                status.add(g);
                return false;
            }
        status.add(g);
        return true;
    }

    /**
     * MESSAGE SENDING
     * This is the primary function to send messages to each peer.
     * Any messages that are sent from here should be encrypted with the peer's public key
     * In the Origin message I include MY Public key
     * @param d
     * @param payload 
     */
    public static void SendMessage(DPCCore.Destination d, DPCGenericObject payload) {
        try (Socket clientSocket = new Socket(d.IPv4, d.Port)) {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    SealedObject sealedObject = null;
	    if(payload.getClass() == SendMessage.class) {
		if(inBox.PeerPublicKeys.containsKey(d.Nick)) {
		    // we have a public key for this person and we need to encrypt the message
		    SendMessage sendMsg = (SendMessage)payload;
		    sealedObject = encryptionHandler.encryptPeerMessage(
			    sendMsg.Message,
			    encryptionHandler.reconstituteKeyFromString(inBox.PeerPublicKeys.get(d.Nick).toString()));
		}
	    }
	    
            DPCGenericObject g = payload;
	    Gson gson = new GsonBuilder().create();
            String json = gson.toJson(g);
            json = "\"" + g.getClass().getName() + "\": " + json;
            JsonElement je1 = gson.toJsonTree(g);
            JsonObject jo1 = new JsonObject();
            jo1.add(g.getClass().getSimpleName(), je1);
	    DPCMessage m = new DPCMessage(d, new Origin(inBox.getIP(), "", inBox.getPort(), Nick, 
		    encryptionHandler.converyKeyToString(encryptionHandler.getPublicKey())), g.getClass().getSimpleName(), jo1);
	    if(sealedObject != null)
		m.setSealedObject(sealedObject);
            //DPCMessage m = new DPCMessage(d, new Origin(inBox.getIP(), "", inBox.getPort(), Nick, ""), g.getClass().getSimpleName(), jo1);
            JsonElement je2 = gson.toJsonTree(m);
            JsonObject jo2 = new JsonObject();
            jo2.add(m.getClass().getSimpleName(), je2);
            String json1 = jo2.toString();
            outToServer.writeBytes(json1);
        } catch (Exception e) {

            /* ELECTION CONDITIONS
             * Due to time constraints this is something of a kludge.
             * Really, this logic should be in the callback ConfirmInUpdateMasterChatList
             * The code is meant to determine if there is an orphaned chat group.
             * There is a use of a dead peer list, and each time an Admin causes a connection
             * exception, it is presumed to be dead, or gone. one all admins are dead, and the 
             * dead peer list is equivilent to the originating contact list, the discoverer
             * will attempt to sieze the group. This peer calls selfElect to posit 
             * itself as the primary contact for the group.
             */
            if (d==MasterChatServer) return;
           StatusOfChatGroup s = getGroup(d.ThreadID);
            // If we are an admin, nevermind
            if (s.adminFlag() || state == DPCConstants.State.ADMIN) return;
            DPCChatGroup group = getChatGroupFromMCL(d.ThreadID);

            for (Destination cg : group.Contacts)
            {
                if (cg.IPv4.equals(d.IPv4) && cg.Port==d.Port)
                    deadPeers.add(getContactByIPPort(d.IPv4,d.Port));//isOnline++;
            }
            if (deadPeers.size()>=group.Contacts.size())
            {
                deadPeers = new ArrayList<>();
                for (Origin i : deadPeers)
                        for (Origin k : s.Contacts)
                            if (k.Nick.equals(i.Nick))
                                s.Contacts.remove(k);
                selfElect(d.ThreadID);
            }
            
            
            
            
        }
    }

    //Send message to a chat group members, where the chat group is identified by thread id.
    public static void SendMessage(String thread, DPCGenericObject payload) {
        //foreach ()
        StatusOfChatGroup cg = null; //status.get(0);
        for (StatusOfChatGroup s : status)
            if (s.ThreadID.equals(thread))
                cg = s;

        if (cg == null) return;
	
        for (Origin o : cg.Contacts) {
            SendMessage(new Destination(o.IPv4, o.IPv6, o.Port, thread, o.Nick), payload);
        }
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

    //When a peer is started it request from the master server a list of all chat groups.
    public static void askForMasterChatList(Destination masterServerDestination) {
        DPCInstance.setCallback(MasterChatServer, "GetMasterChatList", new GetMasterChatList());
    }

    //When a peer is started it request from the master server a list of all chat groups.
       public static void askForMasterChatList() {
        DPCInstance.setCallback(MasterChatServer, "GetMasterChatList", new GetMasterChatList());
    }

    //A peer is trying to elect itself to be an admin in a group and let the master server know that.
    //So it can update its master server chat list.
       public static void selfElect(String thread) {
           DPCChatGroup group = null;
           for (DPCChatGroup g : masterChatList.getChatGroups())
           {
               if (g.ThreadID.equals(thread))
		   group=g;
           }
           group.Contacts = new ArrayList();
           group.Contacts.add(new Destination(inBox.getIP(),"",inBox.getPort(),group.ThreadID));
           setCallback(MasterChatServer, "UpdateMasterChatList", new UpdateMasterChatList(group));
           // DPCInstance.SendMessage(MasterChatServer, group);
           //DPCInstance.setCallback(MasterChatServer, "UpdateMasterChatList", group);
    }
       
    //Keeps the peer in its current state until message has been received.
    public static void setCallback(String who, String s, DPCGenericObject msg)
    {
        //DPCCore.Destination p=new DPCCore.Destination("127.0.0.1", "", 1964, "");
        Origin o = getContact(who);
        DPCCore.Destination d=new DPCCore.Destination(o.IPv4, "", o.Port, "");
        DPCStateMonitor m = new DPCStateMonitor(o,s);
        waiting.add(m);
        m.start();
        
        //r.start();
        DPCInstance.SendMessage(d, new DPCCore.messages.Ping());
				   
    }

    //Keeps the peer in its current state until message has been received.
    public static void setCallback(Destination who, String s, DPCGenericObject msg)
    {
        DPCGenericObject toSend;
        //Origin o = getContactByIPPort(who.IPv4, who.getPort());
        toSend = getContactByIPPort(who.IPv4, who.getPort());
        if (toSend==null)
            toSend = who;
        DPCStateMonitor m = new DPCStateMonitor(toSend,s);
        waiting.add(m);
        m.start();
        
        //r.start();
        DPCInstance.SendMessage(who, msg);
				   
    }
    
    //Keeps the peer in its current state until message has been received.
     public static void setCallback(DPCChatGroup who, String s, DPCGenericObject msg)
    {

        DPCStateMonitor m = new DPCStateMonitor(who,s);
        waiting.add(m);
        m.start();
        
        //r.start();
        DPCInstance.SendMessage(new Destination(who.Contacts.get(0).IPv4,"",who.Contacts.get(0).Port,who.ThreadID), msg);
				   
    }
     
    //Releases the peer from waiting for a message
    public static void ClearWaiting(DPCGenericObject o, String s)
    {
        try{
       for (DPCStateMonitor d : waiting)
       {
           if (d.origin==o && d.command.equals(s))
           {
               waiting.remove(d);
               synchronized(o){
                        o.notify();
                }
           }
       }
        }catch(Exception e){}
        
    }
    
    //Monitors the states the peer
    public static void StateCallback(DPCStateMonitor val) {
        events.MessageReceivedEvent event = null;
        if (waiting.contains(val))
        {
            switch(val.command)
            {
                case "GetMasterChatList":
                    state = DPCConstants.State.NOT_CONNECTED;
                    event = new events.MessageReceivedEvent(inBox, MessageTypes.Error, new DPCError(DPCCore.DPCConstants.ErrorState.INVALID_MASTER_SERVER));
                    eMessageWriter.fireMyEvent(event);
                    
                break;
                    
                case "ConnectToChatGroup":
                    state = DPCConstants.State.PROVISIONED_TO_CONNECTION;
                    event = new events.MessageReceivedEvent(inBox, MessageTypes.Error, new DPCError(DPCCore.DPCConstants.ErrorState.REJECT_CONNECTION));
                    eMessageWriter.fireMyEvent(event);
                    
                break;
                
                case "Ping":
                    event = new events.MessageReceivedEvent(inBox, MessageTypes.Error, new DPCError(DPCCore.DPCConstants.ErrorState.INVALID_MASTER_SERVER));
                    eMessageWriter.fireMyEvent(event);
                    
                break;
            }
            
            // Set States, Respond to people as needed.
        }
    }   
     
    //sets the current state that peer is in.
    public boolean setState(DPCConstants.State newState)
    {
        state = newState;
        return false;
    }
    
    
    

}
