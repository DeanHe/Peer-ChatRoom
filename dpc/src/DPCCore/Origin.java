package DPCCore;
import DPCCore.messages.DPCGenericObject;

import java.io.PrintStream;

/**
 * Origin.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * Origin store the network address of a peer. 
 * DPCMessage stores both Origin and Destination.
 * <Destination>
 * <ipv4>192.168.1.102</ipv4>
 * <ipv6>2001:0:9d38:6ab8:3cee:2058:b8e8:11a5</ipv6>
 * <port>1212</port>
 * <ThreadID>y567de</ThreadID></Destination>
*/

public class Origin extends DPCGenericObject {
    public String IPv4; //InetAddress
    public String IPv6; // Inet6Address
    public int Port;
    public String Nick;
    public String PublicKey;
    //public java.security.PublicKey PublicKeyObject;
    
    public Origin()
    {
        IPv4 = "127.0.0.1";
        IPv6 = "1.1.1.1";
        Port = 1975;
        Nick = "Harpy";
        PublicKey = "PPUUBBLLIICCKKEEYY";
    }
    
    public Origin(String ipv4, String ipv6, int port, String nick, String p_key)
    {
        IPv4 = ipv4;
        IPv6 = ipv6;
        Port = port;
        Nick = nick;
        PublicKey = p_key;
    }
    
    
    @Override
    public boolean equals(Object obj)
    {
        try{
        Origin o = (Origin) obj;
        if (!this.IPv4.equalsIgnoreCase(o.IPv4)) return false;
        if (!this.IPv6.equalsIgnoreCase(o.IPv6)) return false;
        if (this.Port != o.Port) return false;
        if (!this.Nick.equalsIgnoreCase(o.Nick)) return false;
        }catch(Exception e){return false;}
        return true;
    }

    //log function that is used for debugging
    public void log(PrintStream out) {
        out.println("*-------------Origin-------------*");
        out.println("\tIPv4: " + IPv4);
        out.println("\tPort: " + Port);
        out.println("\tNick: " + Nick);
        out.println("\tPublicKey: " + PublicKey);
    }
}
