
package DPCCore.messages;


import java.io.PrintStream;

/**
 * ConnectToChatGroup.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * A request by a peer to connect to a chat group.
 * Display the Stateful feature.
 */
public class ConnectToChatGroup extends DPCGenericObject {
    public String ThreadID;
    public String PublicKey;

    public ConnectToChatGroup(String ThreadID, String PK) {
        this.ThreadID = ThreadID;
        this.PublicKey = PK;
    }

    public void log(PrintStream out) {
        out.println("*-------------ConnectToChatGroup-------------*");
        out.println("\tThreadID: " + ThreadID);
        out.println("\tPublicKey: " + PublicKey);
    }
}
