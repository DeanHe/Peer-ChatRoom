package DPCCore.messages;

import java.io.PrintStream;

/**
 * SendMasterChatList.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * A response to the GetMasterCHatList message by providing a list of known chat groups.
 * Display the Stateful feature.
 */
public class SendMasterChatList extends DPCGenericObject {
    private DPCMasterChatList masterChatList;

    public DPCMasterChatList getMasterChatList() {
        return masterChatList;
    }

    public void setMasterChatList(DPCMasterChatList masterChatList) {
        this.masterChatList = masterChatList;
    }

    public SendMasterChatList(DPCMasterChatList masterChatList) {
        this.masterChatList = masterChatList;
    }

    public SendMasterChatList() {
    }


    public void log(PrintStream out) {
        out.println("*-------------SendMasterChatList-------------*");
        masterChatList.log(out);
    }
}
