package DPCCore;

import DPCCore.messages.DPCMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * author Georgi Simeonov
 * This class keeps all JSON interacions.
 */
public class JSONActions {
    public static DPCMessage parseJSONDPCMessage(Socket socket) {
        BufferedReader inFromClient = null;
        String in = "";
        try {
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            in = inFromClient.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser parser = new JsonParser();
        com.google.gson.JsonObject o = (com.google.gson.JsonObject) parser.parse(in);
        JsonElement e = o.get(o.entrySet().iterator().next().getKey());
        Gson gson = new GsonBuilder().create();
        DPCMessage m = gson.fromJson(e, DPCMessage.class);
        return m;
    }
}
