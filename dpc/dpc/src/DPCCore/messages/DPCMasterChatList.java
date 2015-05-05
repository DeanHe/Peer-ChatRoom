package DPCCore.messages;

import java.util.*;

/**
 * @author AMulroney, Georgi Simeonov
 *         Manages the list of all chat groups.
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
}
