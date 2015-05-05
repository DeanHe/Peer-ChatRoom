package DPCCore.messages;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DPCMasterChatList.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * All the functions of the Chat List.
 * Display the Stateful feature.
 */
public class DPCMasterChatList extends DPCGenericObject {
    private List<DPCChatGroup> listChatGroups;

    public DPCMasterChatList() {
        listChatGroups = new ArrayList<DPCChatGroup>();
    }

    //add chat group to the master chat list
    public void add(DPCChatGroup chatGroup) {
        listChatGroups.add(chatGroup);
    }

    //remove chat group to the master chat list
    public boolean remove(DPCChatGroup chatGroup) {
        return listChatGroups.remove(chatGroup);
    }

    public List<DPCChatGroup> getChatGroups() {
        return listChatGroups;
    }

    public void setChatGroups(List<DPCChatGroup> listChatGroups) {
        this.listChatGroups = listChatGroups;
    }

    public Iterator<DPCChatGroup> getIterator() {
        return listChatGroups.iterator();
    }

    public void log(PrintStream out) {
        out.println("*-------------DPCMAsterChatList-------------*");

        Iterator<DPCChatGroup> i = listChatGroups.iterator();
        while (i.hasNext()) {
            i.next().log(out);
        }
    }
}
