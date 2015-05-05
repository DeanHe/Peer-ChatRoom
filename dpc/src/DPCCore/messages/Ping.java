
package DPCCore.messages;

import java.io.PrintStream;
import java.util.Iterator;

/**
 * Ping.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * To ascertain whether a peer or Admin is still online.
 * Display the Server feature.
 */
public class Ping extends DPCGenericObject {
    public int id;
    
    public Ping()
    {
        
        this.id=(int )(Math.random() * 5000 + 1);
    }

    public void log(PrintStream out) {
        out.println("*-------------Ping-------------*");

        out.println("\tID: " + id);

    }
}
