/*

 */
package DPCCore;

import DPCCore.messages.Destination;
import DPCCore.messages.*;
import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author AMulroney
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

    public DPCServer(String publicIP, int port) throws IOException {
        super(port);
        this.publicIP = publicIP;
    }

    public DPCServer(int port) throws IOException {
        super(port);
        this.publicIP = InetAddress.getLocalHost().getHostAddress();
    }

    public DPCServer(String publicIP, int port, int publicPort) throws IOException {
        super(port);
        this.publicIP = publicIP;
        this.publicPort = publicPort;
    }

    public DPCMasterChatList getMasterChatList() {
        return masterChatList;
    }

    public String getIP() {
        return publicIP;
    }

    public int getPort() {
        if (publicPort > 0)
            return this.publicPort;
        else
            return this.getLocalPort();
    }

    public void setPublicIO(OutputStream out) {
        this.out = new PrintStream(out);
    }

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
                //DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                //clientSentence = inFromClient.readLine();
                //capitalizedSentence = clientSentence.toUpperCase() + '\n';
                //outToClient.writeBytes(capitalizedSentence);

                //   try{
//        Map graph = JsonReader.jsonToMaps(in);
//          String json = JsonWriter.objectToJson(graph);
//          javax.swing.JTextArea ta = (javax.swing.JTextArea)out;
//          ta.append(json);
//                            }catch(Exception e){}

                String in = inFromClient.readLine();
                JsonParser parser = new JsonParser();
                com.google.gson.JsonObject o = (com.google.gson.JsonObject) parser.parse(in);

                JsonElement e = o.get(o.entrySet().iterator().next().getKey());
                //  json = o.toString();
                Gson gson = new GsonBuilder().create();
                DPCMessage m = gson.fromJson(e, DPCMessage.class);
                //All For TESting
                JsonElement je = gson.toJsonTree(m);
                com.google.gson.JsonObject jo = new com.google.gson.JsonObject();
                jo.add(m.getClass().getSimpleName(), je);
                String json1 = jo.toString();
                json1 += "";
                String s;
				
                //All For TESting
                if (!MessageTypes.CONNECT_TO_CHAT_GROUP.equals(m.Command) && !MessageTypes.STATUS_OF_CHAT_GROUP.equals(m.Command)) {
                    if (InetAddress.getLoopbackAddress().toString().equals(socket.getInetAddress().toString()))
                        if (socket.getInetAddress().toString().indexOf(m.Origin.IPv4) != 1) return;
                    if (!DPCInstance.isContact(m.Origin)) return;
                }
		
                // do something with out and json
                switch (m.Command) {
                    case MessageTypes.CONNECT_TO_CHAT_GROUP:
                        //DPCMessage d = new DPCMessage(new Destination(m.Origin.IPv4,m.Origin.IPv6,m.Origin.Port,m.Destination.ThreadID),new Origin());
                        DPCInstance.status.get(0).add(m.Origin);
                        DPCInstance.SendMessage(m.Destination.ThreadID, DPCInstance.status.get(0));

                        break;

                    case MessageTypes.STATUS_OF_CHAT_GROUP:
                        StatusOfChatGroup g = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), StatusOfChatGroup.class);
                        if (DPCInstance.Connected(g)) {
                            //((javax.swing.JTextArea)out).append("\n\n"+g.Title+"\n"+g.Description+"\n\n"+g.WelcomeMessage+"\n\n");
                            //out.print("\n\n"+g.Title+"\n"+g.Description+"\n\n"+g.WelcomeMessage+"\n\n");
                            events.MessageReceivedEvent statusOfChatGrp = new events.MessageReceivedEvent(this, MessageTypes.STATUS_OF_CHAT_GROUP, g);
                            eMessageReceived.fireMyEvent(statusOfChatGrp);
                        }
                        break;

                    case MessageTypes.SEND_MESSAGE:
                        SendMessage sm = null;
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
                                        events.MessageReceivedEvent receiveEvent = new events.MessageReceivedEvent(this, MessageTypes.SEND_MESSAGE, m, sm);
                                        eMessageReceived.fireMyEvent(receiveEvent);
                                    }
                                }
                            }
                        }
                        //sm = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), SendMessage.class);
                        //((javax.swing.JTextArea)out).append("\n"+ m.Origin.Nick+ ": "+sm.Message);//s = m.Command;
                        //mainScreen.displayPeerMessage(m.Origin.Nick, m.Origin.Nick, sm.Message);
                        break;

                    case MessageTypes.EXIT:
                        s = m.Command;
                        break;

                    case MessageTypes.PING:
                        DPCInstance.SendMessage(new Destination(m.Origin.IPv4, m.Origin.IPv6, m.Origin.Port, m.Destination.ThreadID), new PingAck(gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), Ping.class)));
                        //((javax.swing.JTextArea)out).append("\n"+ m.Origin.Nick+ " Pinged you");
                        //out.print("\n"+ m.Origin.Nick+ " Pinged you");
                        events.MessageReceivedEvent pingRcv = new events.MessageReceivedEvent(this, MessageTypes.PING, m, "PING RECEIVED");
                        eMessageReceived.fireMyEvent(pingRcv);
                        break;
                    case MessageTypes.PING_ACK:
                        //Do something
                        //((javax.swing.JTextArea)out).append("\n"+ m.Origin.Nick+ " sent a PingAck");
                        //out.print("\n"+ m.Origin.Nick+ " sent a PingAck");
                        events.MessageReceivedEvent pingSent = new events.MessageReceivedEvent(this, MessageTypes.PING_ACK, m, "PING SENT");
                        eMessageReceived.fireMyEvent(pingSent);
                        break;

                    case MessageTypes.APPOINT:
                        UpdateMasterChatList u = new UpdateMasterChatList();
                        DPCInstance.SendMessage(DPCInstance.getMasterChatServer(), u);
                        //DPCInstance.status.get(0).wait(10000);
                        break;

                    case MessageTypes.APPOINT_ACK:
                        s = m.Command;
                        break;

                    case MessageTypes.SEND_MASTER_CHAT_LIST:
                        SendMasterChatList masterChatListMessage = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), SendMasterChatList.class);
                        masterChatList = masterChatListMessage.getMasterChatList();
                        //  masterChatListMessage.getMasterChatList().getChatGroups()
                        //Note: events handling goes here.
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
        