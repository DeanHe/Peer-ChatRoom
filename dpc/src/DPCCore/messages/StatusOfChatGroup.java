
package DPCCore.messages;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DPCCore.Destination;
import DPCCore.Origin;

/**
 * StatusOfChatGroup.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * A response to the ConnectTochatgroup message by providing a list of connected peers.
 * Display the Stateful feature.
 */
public class StatusOfChatGroup extends DPCGenericObject {
    public String ThreadID;
    public String Title;
    public String Description;
    public String WelcomeMessage;
    public List<Origin> Contacts;
    protected transient boolean asAdmin = false;

    public StatusOfChatGroup(String id, String title, String desc) {
        ThreadID = id;
        Title = title;
        Description = desc;
        Contacts = new ArrayList<Origin>();
    }

     public StatusOfChatGroup(String id, String title, String desc, boolean admin) {
        ThreadID = id;
        Title = title;
        Description = desc;
        Contacts = new ArrayList<Origin>();
        asAdmin = admin;
    }
    
    public void add(Origin d) {
        Contacts.add(d);
    }
    public boolean adminFlag()
    {
        return asAdmin;
    
    }
        public void setAdminFlag(boolean b)
    {
        asAdmin = b;
    }
            
    public void remove(String id) {
//        Iterator<Origin> i = Contacts.iterator();
//        while (i.hasNext()) {
//            Origin o = i.next();
//            if (o.ThreadID.equals(id))
//                i.remove();
//        }
    }

    public List<Origin> getContacts() {
        return Contacts;
    }

    public void log(PrintStream out) {
        out.println("*-------------StatusOfChatGroup-------------*");
        out.println("\tThreadID: " + ThreadID);
        out.println("\tTitle: " + Title);
        out.println("\tDescription: " + Description);
        out.println("\tWelcomeMessage: " + WelcomeMessage);
        Iterator<Origin> i = Contacts.iterator();
        while (i.hasNext()) {
            i.next().log(out);
        }
    }
}
