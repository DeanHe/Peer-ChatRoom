package DPCCore.messages;

import java.io.PrintStream;

/**
 * UpdateMasterChatList.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * To register a chat group with the Mast List Server.
 * Display the Concurrent feature.
 */
public class UpdateMasterChatList extends DPCGenericObject {
    private DPCChatGroup ChatGroup;

    public UpdateMasterChatList(DPCChatGroup g)
    {
        ChatGroup = g;
    }
    
    public UpdateMasterChatList(String threadID, String title, String desc, boolean admin)
    {
        ChatGroup = new DPCChatGroup(threadID,title,desc,admin);
    }
    
    public DPCChatGroup getChatGroup()
    {
        return ChatGroup;
    }
}



/*package DPCCore.messages;

import java.io.PrintStream;

/**
 * author Georgi Simeonov
 */
/*public class UpdateMasterChatList extends DPCGenericObject {
    private DPCMasterChatList masterChatList;

    public DPCMasterChatList getMasterChatList() {
        return masterChatList;
    }

    public void setMasterChatList(DPCMasterChatList masterChatList) {
        this.masterChatList = masterChatList;
    }

    public void log(PrintStream out) {
        out.println("*-------------UpdateMasterChatList-------------*");
        masterChatList.log(out);
    }
}*/
