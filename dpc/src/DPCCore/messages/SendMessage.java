
package DPCCore.messages;

import java.io.PrintStream;
import javax.crypto.SealedObject;

/**
 * SendMessage.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * A basic message for conversation between two peers.
 * Display the Concurrent feature.
 */
public class SendMessage extends DPCGenericObject {
    public String Message;
    
    public SendMessage(String Message)
    {
        this.Message=Message;
    }

    public void log(PrintStream out) {
        out.println("*-------------SendMessage-------------*");
        out.println("\tMessage: " + Message);
    }
}
