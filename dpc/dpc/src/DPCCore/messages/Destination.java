/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DPCCore.messages;

/**
 * @author AMulroney
 *         <Destination>
 *         <ipv4>192.168.1.102</ipv4>
 *         <ipv6>2001:0:9d38:6ab8:3cee:2058:b8e8:11a5</ipv6>
 *         <port>1212</port>
 *         <ThreadID>y567de</ThreadID></Destination>
 */
public class Destination extends DPCGenericObject {
    public String IPv4; //InetAddress
    public String IPv6; // Inet6Address
    public int Port;
    public String Nick;
    public String ThreadID;

    public Destination() {
        IPv4 = "127.0.0.1";
        IPv6 = "1.1.1.1";
        Port = 1975;
        ThreadID = "Allo";
    }
    
    public Destination(String ipv4, String ipv6, int port, String thread)
    {
        IPv4 = ipv4;
        IPv6 = ipv6;
        Port = port;
        ThreadID = thread;

    }
    
    public Destination(String ipv4, String ipv6, int port, String thread, String sNick)
    {
        IPv4 = ipv4;
        IPv6 = ipv6;
        Port = port;
        ThreadID = thread;
	    Nick = sNick;
    }

    public int getPort() {
        return Port;
    }

    public String getIPv4() {
        return IPv4;
    }
}
