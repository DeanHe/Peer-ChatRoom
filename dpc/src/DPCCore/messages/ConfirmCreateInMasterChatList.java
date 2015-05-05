package DPCCore.messages;

/**
 * ConfirmCreateInMasterChatList.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * Confirms whether the initial UpdateMasterChatList has been successful for the calling peer.
 * Display the Server feature.
 */
public class ConfirmCreateInMasterChatList extends DPCGenericObject{
     private DPCChatGroup ChatGroup;
    public ConfirmCreateInMasterChatList(DPCChatGroup group) {
        ChatGroup = group;
    }
    
    public DPCChatGroup getChatGroup()
    {
        return ChatGroup;
    }
}
