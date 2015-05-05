/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DPCCore.messages;

import java.util.ArrayList;
import java.util.List;
import DPCCore.Origin;

/**
 *
 * @author AMulroney
 */
public class StatusOfChatGroup extends DPCGenericObject {
    public String ThreadID;
    public String Title;
    public String Description;
    public String WelcomeMessage;
    public List<Origin> Contacts;
    private transient boolean asAdmin = false;

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
}
