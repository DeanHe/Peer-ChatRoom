/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DPCCore.messages;

import java.util.*;
import DPCCore.messages.Destination;

/**
 * @author AMulroney
 */
public class DPCChatGroup extends DPCGenericObject {
    public String ThreadID;
    public String Title;
    public String Description;
    public List<Destination> Contacts;
    transient private boolean asAdmin = false;

    public DPCChatGroup(String threadID, String title, String desc) {
        ThreadID = threadID;
        Title = title;
        Description = desc;
        Contacts = new ArrayList<Destination>();
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
}
