
package DPCCore.messages;


import java.io.PrintStream;

/**
 * MessageTypes.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * A response to the Ping message.
 * Display the Server feature.
 */
public class PingAck extends DPCGenericObject {
    public int id;
    
    public PingAck(Ping ping)
    {
        this.id = ping.id+1;
    }


    public void log(PrintStream out) {
        out.println("*-------------PingAck-------------*");
        out.println("\tID: " + id);
    }
}
