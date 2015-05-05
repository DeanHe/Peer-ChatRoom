package DPCCore.messages;

/**
 * author Georgi Simeonov
 *
 */
public class UpdateMasterChatList extends DPCGenericObject {
    private DPCMasterChatList masterChatList;
    public DPCMasterChatList getMasterChatList() {
        return masterChatList;
    }

    public void setMasterChatList(DPCMasterChatList masterChatList) {
        this.masterChatList = masterChatList;
    }
}
