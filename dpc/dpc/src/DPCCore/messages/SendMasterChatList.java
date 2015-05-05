package DPCCore.messages;

/**
 * Author Georgi Simeonov
 *
 * This is the message type for sending the master chat list.
 */
public class SendMasterChatList extends DPCGenericObject {
    private DPCMasterChatList masterChatList;

    public DPCMasterChatList getMasterChatList() {
        return masterChatList;
    }

    public void setMasterChatList(DPCMasterChatList masterChatList) {
        this.masterChatList = masterChatList;
    }

    public SendMasterChatList() {
    }
}
