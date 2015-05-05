package DPCCore;

import DPCCore.messages.Destination;
import DPCCore.messages.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * @author Andrew Mulroney, Georgi Simeonov
 * The master server listens for peers and send them the chat room list.
 */
public class DPCMasterServer extends ServerSocket implements Runnable {

    private String peer;
    private boolean run = false;
    private Object out;
    private DPCChatGroup chatGroups; //initial version for now only one
    //private List<Origin>

    public DPCMasterServer(String peer, int port) throws IOException {
        super(port);
        this.peer = peer;
    }

    public DPCMasterServer(int port) throws IOException {
        super(port);
        this.peer = InetAddress.getLocalHost().getHostAddress();
    }

    public String getIP() {
        return peer;
    }

    public int getPort() {
        return this.getLocalPort();
    }

    public void setPublicIO(Object out) {
        this.out = out;
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
            switch (m.Command) {
                case MessageTypes.GET_MASTER_CHAT_LIST:
                    //send the chat group to the client
                    int port = socket.getPort();
                    String ip = socket.getInetAddress().getHostAddress();
                    DPCInstance.SendMessage(new Destination(ip, "", port, MessageTypes.SEND_MASTER_CHAT_LIST), new SendMasterChatList());
                    break;
                case MessageTypes.UPDATE_MASTER_CHAT_LIST:
                    Gson gson = new GsonBuilder().create();
                    UpdateMasterChatList updateMasterChatList = gson.fromJson(m.Message.getAsJsonObject().entrySet().iterator().next().getValue(), UpdateMasterChatList.class);
                    //ConfirmUpdateInMasterChatList and ConfirmCreateInMasterChatList
                    break;

            }
        }
    }

}
        