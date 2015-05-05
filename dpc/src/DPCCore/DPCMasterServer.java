package DPCCore;


import DPCCore.messages.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;


/**
 * StartMasterServer.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * The master server listens for peers and send them the master chat group list.
 */
public class DPCMasterServer extends ServerSocket implements Runnable {

    private String peer;
    private boolean run = false;
    private Object out;
    private DPCMasterChatList masterChatList = new DPCMasterChatList();
    //private List<Origin>

    //Constructor setting a peer and a port
    public DPCMasterServer(String peer, int port) throws IOException {
        super(port);
        this.peer = peer;
    }

    //Constructor setting a port
    public DPCMasterServer(int port) throws IOException {
        super(port);
        this.peer = InetAddress.getLocalHost().getHostAddress();
    }

    //Setting a master chat group list. It is used for testing purposes.
    public void setMasterChatList(DPCMasterChatList masterChatList) {
        this.masterChatList = masterChatList;
    }

    //Get the IP address of the master server
    public String getIP() {
        return peer;
    }

    //Get the master server listening port
    public int getPort() {
        return this.getLocalPort();
    }

    //Sets logging method for the server
    public void setPublicIO(Object out) {
        this.out = out;
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

    // Send messages from the master server to the peers.
    public static void SendMessageServer(DPCCore.Destination d, String ip, int port,  DPCGenericObject payload) {
        try (Socket clientSocket = new Socket(d.IPv4, d.Port)) {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());


            DPCGenericObject g = payload;
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(g);
            json = "\"" + g.getClass().getName() + "\": " + json;
            JsonElement je1 = gson.toJsonTree(g);
            JsonObject jo1 = new JsonObject();
            jo1.add(g.getClass().getSimpleName(), je1);
            DPCMessage m = new DPCMessage(d, new Origin(ip, "", port, "Nick", ""), g.getClass().getSimpleName(), jo1);
            JsonElement je2 = gson.toJsonTree(m);
            JsonObject jo2 = new JsonObject();
            jo2.add(m.getClass().getSimpleName(), je2);
            String json1 = jo2.toString();
            outToServer.writeBytes(json1);
        } catch (Exception e) {

        }
    }


    /**
     * Class that manages incoming connections at the server.
     * One thread per connection allows multiple simultaneous connections (in a multi-threaded environment obviously)
     */
    class InnerSocketHandler extends Thread {
        private Socket socket;
        //private Network network;

        public InnerSocketHandler(Socket socket, Object out) {
            this.socket = socket;
        }

        public void run() {
            DPCMessage m = JSONActions.parseJSONDPCMessage(socket);
            m.log(System.out);
            int port = m.Origin.Port;
            String ip = m.Origin.IPv4;

            //types of messages received
            switch (m.Command) {
                // this is a request from a peer to send the master chat list
                case MessageTypes.GET_MASTER_CHAT_LIST:

                    //send the chat group to the client
                    SendMasterChatList smc = new SendMasterChatList(masterChatList);
                    smc.log(System.out);
                    SendMessageServer(new Destination(ip, "", port, MessageTypes.SEND_MASTER_CHAT_LIST), ip, port, smc);
                    break;
                    //this is a request from the peer to update master chat list
                case MessageTypes.UPDATE_MASTER_CHAT_LIST:
                    Gson gson = new GsonBuilder().create();
                    UpdateMasterChatList updateMasterChatList = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), UpdateMasterChatList.class);
                    DPCChatGroup cg = updateMasterChatList.getChatGroup();
                    Iterator<DPCChatGroup> masterIter = masterChatList.getIterator();

                    
                        boolean matched = false;
                        while(masterIter.hasNext()) {
                            DPCChatGroup tmpMasterCH = masterIter.next();
                            // if the group already exists, update it
                            if (tmpMasterCH.getThreadID().equalsIgnoreCase(updateMasterChatList.getChatGroup().getThreadID())) {
                                //update the Chat Group
                                masterChatList.remove(tmpMasterCH);
                                masterChatList.add(updateMasterChatList.getChatGroup());
                                
                                SendMessageServer(new Destination(ip, "", port, MessageTypes.CONFIRM_UPDATE_IN_MASTER_CHAT_LIST), ip, port, new ConfirmUpdateInMasterChatList(cg));
                                matched = true;
                                //}
                            }
                        }
                        //if there is no match then it is a new chat group, so add it.
                        if (!matched) {
                            masterChatList.add(updateMasterChatList.getChatGroup());
                            SendMessageServer(new Destination(ip, "", port, MessageTypes.CONFIRM_CREATE_IN_MASTER_CHAT_LIST), ip, port, new ConfirmCreateInMasterChatList(cg));
                        }
                    //}
                    break;

            }
        }
        
    }

    //Checks whether a given a address is a part of a chat group.
    public static boolean originIsValid(DPCChatGroup group, Origin o)
    {
            for (Destination d : group.Contacts)
            {
                if (d.IPv4.equals(o.IPv4) && d.Port==o.Port)
                    return true;
            }
            return false;
    }
}
        
