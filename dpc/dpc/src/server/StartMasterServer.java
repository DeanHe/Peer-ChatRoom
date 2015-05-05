package MoreTesting;

import DPCCore.DPCConstants;
import DPCCore.DPCMasterServer;

import java.io.IOException;

/**
 * @author Georgi Simeonov
 */
public class StartMasterServer {
    public static void main(String[] args) {
        DPCMasterServer masterServer = null;
        try {
            masterServer = new DPCMasterServer(DPCConstants.MASTER_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        masterServer.start();
    }
}
