
package DPCCore.messages;
import DPCCore.messages.*;
import DPCCore.*;
import com.google.gson.*;

import java.io.PrintStream;
import javax.crypto.SealedObject;
/**
 * DPCMessage.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * Set of entities that convey information regarding chat venues.
 * Display the Client feature.
 */

/**

 @author AMulroney

<DPCMessage>
<Version>1.0</Version>
<Destination><ipv4>192.168.1.102</ipv4><ipv6>2001:0:9d38:6ab8:3cee:2058:b8e8:11a5</ipv6><port>1212</port><ThreadID>y567de</ThreadID></Destination>
<Origin><ipv4>192.168.1.102</ipv4><ipv6>2001:0:9d38:6ab8:3cee:2058:b8e8:11a5</ipv6><port>1212</port><Nick>HarryHotspur</Nick></Origin>
<Command></Command>
<Message><msg><msg1>One</msg1><msg2>Two</msg2></msg></Message>
</DPCMessage>

{
  "DPCMessage": {
    "Version": "1.0",
    "Destination": {
      "ipv4": "192.168.1.102",
      "ipv6": "2001:0:9d38:6ab8:3cee:2058:b8e8:11a5",
      "port": "1212",
      "ThreadID": "y567de"
    },
    "Origin": {
      "ipv4": "192.168.1.102",
      "ipv6": "2001:0:9d38:6ab8:3cee:2058:b8e8:11a5",
      "port": "1212",
      "Nick": "HarryHotspur"
    },
    "Message": {
      "msg": {
        "msg1": "One",
        "msg2": "Two"
      }
    }
  }
}
 */
public class DPCMessage extends DPCGenericObject {
    public double Version = 1.0;
    public Destination Destination;
    public Origin Origin;
    public String Command;
    public JsonElement Message;
//<Destination><ipv4>192.168.1.102</ipv4><ipv6>2001:0:9d38:6ab8:3cee:2058:b8e8:11a5</ipv6><port>1212</port><ThreadID>y567de</ThreadID></Destination>
//<Origin><ipv4>192.168.1.102</ipv4><ipv6>2001:0:9d38:6ab8:3cee:2058:b8e8:11a5</ipv6><port>1212</port><Nick>HarryHotspur</Nick></Origin>
    //<Message><msg><msg1>One</msg1><msg2>Two</msg2></msg></Message>
    public DPCMessage(Destination d,Origin o, String c)
    {
        Destination = d;
        Origin = o;
        Command = c;
        //Message = msg;
    }
    public DPCMessage(Destination d,Origin o, String c, JsonObject msg)
    {
        Destination = d;
        Origin = o;
        Command = c;
        Message = msg;
    }
    
    private SealedObject sealedObject;    
    public void setSealedObject(SealedObject sealed){
	sealedObject = sealed;
    }
    
    public SealedObject getSealedObject() {
	return sealedObject;
    }

    public void log(PrintStream out) {
        out.println("*-------------DPCMessage-------------*");
        out.println("\tVersion: " + Version);
        this.Destination.log(out);
        this.Origin.log(out);
        out.println("\t" + Command);
    }
}
