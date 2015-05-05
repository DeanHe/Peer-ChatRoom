package server;

/**
 * StartMasterServer.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * It starts the MasterChatServer with a sample chat room and a few peers.
 */
import DPCCore.DPCConstants;
import DPCCore.DPCMasterServer;
import DPCCore.messages.DPCChatGroup;
import DPCCore.messages.DPCMasterChatList;

import DPCCore.Destination;
import java.io.IOException;
import java.net.InetAddress;

/**
 * @author Georgi Simeonov
 */
public class StartMasterServer {
    public static void main(String[] args) {

        //Sample chat group HAJ123.
        DPCChatGroup chatGroup = new DPCChatGroup("HAJ123", "Haj To Utopia", "How The Ghadar Movement Charted Global Radicalism and attempted to Overthrow the British Empire. BLAH BLAH BLAH Green Turbans BLAH BLAH BLAH Irish Women in San Francisco in saris.... Roger Casement... Free Khalistan!");
        try{
          //Adding 2 peers. If the MasterScreen is called without parameters twice the two peers below are created. 
        chatGroup.add(new Destination(InetAddress.getLocalHost().getHostAddress(), "", 1964, "HAJ123", "Wonder Man"));
        chatGroup.add(new Destination(InetAddress.getLocalHost().getHostAddress(), "", 1871, "HAJ123", "Octavius Catto"));
        }catch (Exception e){}
        //chatGroup.add(new Destination("127.0.0.1", "", 1916, "HAJ123", "James Connally"));
        DPCMasterChatList masterChatList = new DPCMasterChatList();
        //The sample chat group is added to the master server as a presentation.
        masterChatList.add(chatGroup);


        DPCMasterServer masterServer = null;
        try {
          //starting the master server on a predefined port 1977
            masterServer = new DPCMasterServer(DPCConstants.MASTER_SERVER_PORT);

            //setting the master server with a sample chat group
            masterServer.setMasterChatList(masterChatList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //starting the master server
        masterServer.start();
    }
}
