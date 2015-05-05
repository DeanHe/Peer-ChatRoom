package DPCCore.messages;

/**
 * ConfirmUpdateInMasterChatList.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * Confirms whether the initial UpdateMasterChatList has been successful for the calling peer.
 * Display the Server feature.
 */
public class ConfirmUpdateInMasterChatList extends DPCGenericObject{
        private DPCChatGroup ChatGroup;
    public ConfirmUpdateInMasterChatList(DPCChatGroup group) {
        ChatGroup = group;
    }
    
    public DPCChatGroup getChatGroup()
    {
        return ChatGroup;
    }
}
