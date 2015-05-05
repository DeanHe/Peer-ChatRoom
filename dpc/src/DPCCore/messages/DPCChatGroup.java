
package DPCCore.messages;

import java.io.PrintStream;
import java.security.PublicKey;
import java.util.*;
import DPCCore.*;
/**
 * DPCChatGroup.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * This entity is a JSON-defined description of a single registered chat group.
 * Display the Stateful feature.
 */
public class DPCChatGroup extends DPCGenericObject {
    public String ThreadID;
    public String Title;
    public String Description;
    public List<Destination> Contacts;
    transient private boolean asAdmin = false;

    public DPCChatGroup(String threadID, String title, String desc) {
        ThreadID = threadID;
        // a unique ID for each chat group
        Title = title;
        // a short title for the chat group
        Description = desc;
        // an optional field intended to provide more detail
        Contacts = new ArrayList<Destination>();
        // a list of publicly available contacts
    }

    public DPCChatGroup(String threadID, String title, String desc, boolean admin) {
        ThreadID = threadID;
        Title = title;
        Description = desc;
        Contacts = new ArrayList<Destination>();
        asAdmin = admin;
    }

    public void add(Destination d) {
        Contacts.add(d);
    }

    public void remove(String threadID) {
        Iterator<Destination> i = Contacts.iterator();
        while (i.hasNext()) {
            Destination d = i.next();
            if (d.ThreadID.equals(threadID))
                i.remove();
        }
    }

    public List<Destination> getContacts() {
        return Contacts;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getThreadID() {
        return ThreadID;
    }

    public void setThreadID(String threadID) {
        ThreadID = threadID;
    }

    public void log(PrintStream out) {
        out.println("*-------------DPCChatGroup-------------*");
        out.println("\tThreadID: " + ThreadID);
        out.println("\tTitle: " + Title);
        out.println("\tDescription: " + Description);

        Iterator<Destination> i = Contacts.iterator();
        while (i.hasNext()) {
            i.next().log(out);
        }
    }
}
