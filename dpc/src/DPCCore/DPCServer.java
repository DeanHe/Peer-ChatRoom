package DPCCore;

import static DPCCore.DPCInstance.MasterChatServer;
import DPCCore.messages.*;
import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * DPCServer.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * DPCServer is the main class that handles receiving messages. 
 * It fulfils requirements for concurency since a peer(which has an instance of DPCServer)
 * can handled multiple incoming connectionf through DPCServe.
 */
public class DPCServer extends ServerSocket implements Runnable {
    private client.MainScreen mainScreen;
    private String publicIP;
    private boolean run = false;
    private int publicPort = 0;
    private PrintStream out;
    private events.MessageReceived eMessageReceived;
    //private List<Origin>
    private DPCMasterChatList masterChatList;
    public HashMap PeerPublicKeys = new HashMap(); // This will be the peer's name (NICK) and the peer's PublicKey string

    //DPCServer constructor 
    public DPCServer(String publicIP, int port) throws IOException {
        super(port);
        this.publicIP = publicIP;
    }

    //DPCServer constructor 
    public DPCServer(int port) throws IOException {
        super(port);
        this.publicIP = InetAddress.getLocalHost().getHostAddress();
    }

    //DPCServer constructor 
    public DPCServer(String publicIP, int port, int publicPort) throws IOException {
        super(port);
        this.publicIP = publicIP;
        this.publicPort = publicPort;
    }

    //Retuns master chat list.
    public DPCMasterChatList getMasterChatList() {
        return masterChatList;
    }

    //Returns peer's ip address
    public String getIP() {
        return publicIP;
    }

    //Returns peer's port
    public int getPort() {
        if (publicPort > 0)
            return this.publicPort;
        else
            return this.getLocalPort();
    }

    //Setting a way to log information 
    public void setPublicIO(OutputStream out) {
        this.out = new PrintStream(out);
    }

    //Setting a way to interact with events
    public void setPublicIO(events.MessageReceived evt) {
        this.eMessageReceived = evt;
    }

    /**
     * Provides a mapping between the output stream component and the nick name of the peer that sent the message
     *
     * @param out
     * @param sNick
     */
    //public void addPublicIO(OutputStream out, String sNick) {
    //peerOutput.put(sNick, out);
    //}
    public void setMainScreen(client.MainScreen m) {
        this.mainScreen = m;
    }

    //Concurent Requirement
    //Creates a new helping class InnerSocketHandler for each incoming call.
    @Override
    public void run() {
        if (run == false) run = true;
        while (run) {
            try {
                Socket socket = accept();
                new InnerSocketHandler(socket, out).start();
            } catch (IOException e) {

                run = false;
            }
        }
    }

    // starts the server
    public void start() {
        if (run) return; //

        run = true;
        new Thread(this).start(); // runs the server thread
    }

    // stops the server
    protected void stop() {
        run = false;
    }

    // checks if the server is running
    protected boolean isRunning() {
        return run;
    }

    /**
     * Inner sockerhandler
     */
    class InnerSocketHandler extends Thread {
        private Socket socket;
        //private Network network;

        public InnerSocketHandler(Socket socket, Object out) {
            this.socket = socket;
            //this.network = peer.network();
        }

        //receiving messages
        public void run() {
            try {
                SocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                String in = inFromClient.readLine();
                JsonParser parser = new JsonParser();
                com.google.gson.JsonObject o = (com.google.gson.JsonObject) parser.parse(in);

                JsonElement e = o.get(o.entrySet().iterator().next().getKey());
                //  json = o.toString();
                Gson gson = new GsonBuilder().create();
                DPCMessage m = gson.fromJson(e, DPCMessage.class);
		System.out.println("Do we have a sealed object? "+m.getSealedObject());
                //All For TESting
                                    JsonElement je = gson.toJsonTree(m);
                                    com.google.gson.JsonObject jo = new com.google.gson.JsonObject();
                                    jo.add(m.getClass().getSimpleName(), je);
                                    String json1 = jo.toString();
                                    json1 += "";
                                    String s;
				
                //All For TESting
//                if (!MessageTypes.CONNECT_TO_CHAT_GROUP.equals(m.Command) && !MessageTypes.STATUS_OF_CHAT_GROUP.equals(m.Command) && !MessageTypes.PING_ACK.equals(m.Command)) {
//                    if (InetAddress.getLoopbackAddress().toString().equals(socket.getInetAddress().toString()))
//                        if (socket.getInetAddress().toString().indexOf(m.Origin.IPv4) != 1) return;
//                    if (!DPCInstance.isContact(m.Origin)) return;
//                }
		
                // do something with out and json
                 if (m.Version!=1.0) DPCInstance.SendMessage(new Destination(m.Origin.IPv4,"",m.Origin.Port,"ERROR"), new DPCError(DPCConstants.ErrorState.REJECT_CONNECTION));
                //logs the incoming message to standart output
               
                m.log(System.out);
		System.out.println(" I received a command: "+m.Command);

                //checks the message type
                switch (m.Command) {

                    //A peer is trying to connect to chat group
                    case MessageTypes.CONNECT_TO_CHAT_GROUP:
                        DPCInstance.syncGroup(m.Origin, m.Destination.ThreadID);//DPCInstance.getGroup(m.Destination.ThreadID).add(m.Origin);
                        //sends to the peer the chat group information
                        DPCInstance.SendMessage(m.Destination.ThreadID, DPCInstance.getGroup(m.Destination.ThreadID));
                        
                        break;

                    //a peer is asking about the status of a chat group (a list of all peer present)
                    case MessageTypes.STATUS_OF_CHAT_GROUP:
                        //Stateful requirement
                        DPCInstance.state = DPCConstants.State.PEER_STATE;
                        StatusOfChatGroup g = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), StatusOfChatGroup.class);
                        g.log(System.out);
                       DPCInstance.Connected(g); 
                        {
                            events.MessageReceivedEvent statusOfChatGrp = new events.MessageReceivedEvent(this, MessageTypes.STATUS_OF_CHAT_GROUP, g);
                            eMessageReceived.fireMyEvent(statusOfChatGrp);
                        }
                        //sends the message to the peer that is asking for it
                        DPCInstance.ClearWaiting(DPCInstance.getChatGroupFromMCL(m.Destination.ThreadID), "ConnectToChatGroup");
                        break;

                    //sends a normal chat message for communication purposes.
                    case MessageTypes.SEND_MESSAGE:
                        SendMessage sm = null;
			if(m.Destination.ThreadID == null || m.Destination.toString().equals("")) {			
			    for (Iterator<StatusOfChatGroup> i1 = DPCInstance.status.iterator(); i1.hasNext(); ) {
				StatusOfChatGroup cg = i1.next();
				if (cg.ThreadID.equals(m.Destination.ThreadID)) {
				    for (Iterator<Origin> i2 = cg.Contacts.iterator(); i2.hasNext(); ) {
					Origin from = i2.next();
					// ***** Should we not be checking "NICK" here instead???
					if (from.IPv4.equals(m.Origin.IPv4) && from.Nick.equals(m.Origin.Nick)) {					
					    sm = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), SendMessage.class);
					    //((javax.swing.JTextArea)out).append("\n"+ m.Origin.Nick+ ": "+sm.Message);//s = m.Command;
					    //mainScreen.displayPeerMessage(m.Origin.Nick, m.Origin.Nick, sm.Message);
					    //out.print("\n"+ m.Origin.Nick+ ": "+sm.Message);

					    // Get this person's public key if they have one
					    try{
						if(!m.Origin.Nick.equals(DPCInstance.getNick())) {
						    PeerPublicKeys.put(m.Origin.Nick, m.Origin.PublicKey); 
						    System.out.println("I put the person's key in my table: "+m.Origin.Nick);
						}
					    }catch(Exception eeee){}
					    events.MessageReceivedEvent receiveEvent = new events.MessageReceivedEvent(this, MessageTypes.SEND_MESSAGE, m, sm);
					    eMessageReceived.fireMyEvent(receiveEvent);
					}
				    }
				}
			    }
			}
			else {
			    // Get the peer's public key
			    try{
				if(!m.Origin.Nick.equals(DPCInstance.getNick())) {
				    PeerPublicKeys.put(m.Origin.Nick, m.Origin.PublicKey); 
				    System.out.println("I put the person's key in my table: "+m.Origin.Nick);
				}
			    }catch(Exception eeee){}
			    // It's a private message so we need to trigger the event
			    sm = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), SendMessage.class);
			    events.MessageReceivedEvent receiveEvent = new events.MessageReceivedEvent(this, MessageTypes.SEND_MESSAGE, m, sm);
			    eMessageReceived.fireMyEvent(receiveEvent);			    
			}
                        //sm = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), SendMessage.class);
                        //((javax.swing.JTextArea)out).append("\n"+ m.Origin.Nick+ ": "+sm.Message);//s = m.Command;
                        //mainScreen.displayPeerMessage(m.Origin.Nick, m.Origin.Nick, sm.Message);
                        break;

                    case MessageTypes.EXIT:
                        s = m.Command;
                        break;

                    case MessageTypes.PING:
                        DPCInstance.SendMessage(new Destination(m.Origin.IPv4, m.Origin.IPv6, m.Origin.Port, m.Destination.ThreadID), new PingAck(gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), Ping.class)));//new PingAck(gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), PingAck.class))
                        //((javax.swing.JTextArea)out).append("\n"+ m.Origin.Nick+ " Pinged you");
                        //out.print("\n"+ m.Origin.Nick+ " Pinged you");
                        events.MessageReceivedEvent pingRcv = new events.MessageReceivedEvent(this, MessageTypes.PING, m, "PING RECEIVED");
                        eMessageReceived.fireMyEvent(pingRcv);
                        break;
                    case MessageTypes.PING_ACK:
                        //Do something
                        //((javax.swing.JTextArea)out).append("\n"+ m.Origin.Nick+ " sent a PingAck");
                        //out.print("\n"+ m.Origin.Nick+ " sent a PingAck");
                        Origin o1 = DPCInstance.getContact(m.Origin.Nick);
                        DPCInstance.ClearWaiting(o1, "Ping");
                        events.MessageReceivedEvent pingSent = new events.MessageReceivedEvent(this, MessageTypes.PING_ACK, m, "PING SENT");
                        eMessageReceived.fireMyEvent(pingSent);
                        break;

                    case MessageTypes.APPOINT:
                        DPCInstance.state = DPCConstants.State.ELECTION_STATE;
                        

                        break;

                    case MessageTypes.APPOINT_ACK:
                        //nothing to do here -- s = m.Command;
                        break;

                   case MessageTypes.CONFIRM_CREATE_IN_MASTER_CHAT_LIST:
                       
                       DPCInstance.state =  DPCConstants.State.ADMIN; 
                       ConfirmCreateInMasterChatList cgc = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), ConfirmCreateInMasterChatList.class);
                       StatusOfChatGroup scgc = new StatusOfChatGroup(cgc.getChatGroup().ThreadID, cgc.getChatGroup().Title, "", true);
                       scgc.Contacts.add(new Origin(getIP(),"",getPort(),DPCInstance.Nick,""));
                       DPCInstance.status.add(scgc);
                       DPCInstance.setGroup(scgc);
                       
                       DPCInstance.SendMessage(scgc.ThreadID, DPCInstance.getGroup(scgc.ThreadID));
                                           //        events.MessageReceivedEvent statusOfChatGrp = new events.MessageReceivedEvent(this, MessageTypes.STATUS_OF_CHAT_GROUP, cgc);
                            //eMessageReceived.fireMyEvent(statusOfChatGrp);
                   break;
                   case MessageTypes.CONFIRM_UPDATE_IN_MASTER_CHAT_LIST: 
                       DPCInstance.state =  DPCConstants.State.ADMIN; 
                       ConfirmUpdateInMasterChatList cgu = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), ConfirmUpdateInMasterChatList.class);
                       StatusOfChatGroup scgu = DPCInstance.getGroup(cgu.getChatGroup().ThreadID);
                       scgu.setAdminFlag(true);
                       //StatusOfChatGroup scgu = new StatusOfChatGroup(cgu.getChatGroup().ThreadID, cgu.getChatGroup().Title, "", true);
                       DPCInstance.setGroup(scgu);
                       DPCInstance.SendMessage(scgu.ThreadID, DPCInstance.getGroup(scgu.ThreadID));
                       
                       //events.MessageReceivedEvent statusOfChatGrp1 = new events.MessageReceivedEvent(this, MessageTypes.STATUS_OF_CHAT_GROUP, cgu);
                       //eMessageReceived.fireMyEvent(statusOfChatGrp1);
                       break;
                    case MessageTypes.SEND_MASTER_CHAT_LIST:
                        
                        DPCInstance.state = DPCConstants.State.PROVISIONED_TO_CONNECTION;
                        
                        SendMasterChatList masterChatListMessage = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), SendMasterChatList.class);
                        DPCInstance.ClearWaiting(DPCInstance.MasterChatServer, "GetMasterChatList");
                        DPCInstance.masterChatList = masterChatListMessage.getMasterChatList();
                        masterChatList = masterChatListMessage.getMasterChatList();
                        events.MessageReceivedEvent mcl = new events.MessageReceivedEvent(this, MessageTypes.SEND_MASTER_CHAT_LIST, masterChatList);
                        
                        eMessageReceived.fireMyEvent(mcl);
                        //  masterChatListMessage.getMasterChatList().getChatGroups()
                        //Note: events handling goes here.
                        break;
		    default: System.err.println("SOMEHOW I GOT HERE - received "+m.Command+" from "+m.Origin.Nick); break;

                }
                
                
                
                /*Channel channel = new Channel(socket);
                Message inMsg, outMsg;
                inMsg = channel.receive();
                
                if(inMsg.is(Message.JOIN_REQUEST)) {
                    do_JOIN_REQUEST(channel);
                }
                else if(inMsg.is(Message.NEW_PEER)) {
                    do_NEW_PEER(channel, inMsg);
                }
                else if(inMsg.is(Message.DEAD_PEER)) {
                    do_DEAD_PEER(channel, inMsg);
                }
                else
                    peer.do_commands(channel, inMsg); // this method describes how the peer manages received commands
                

                channel.close();*/
                //Log.write("Closed connection with " + remoteSocketAddress, FileDescriptor.out);

            } catch (IOException e) {
            }
        }
    }

}
        
