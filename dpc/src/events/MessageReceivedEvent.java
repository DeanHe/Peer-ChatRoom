/**
 * MessageReceivedEvent.java
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * @date May 13, 2013
 * 
 * The DPC protocol uses an event trigger to update output. The protocol library 
 * cannot be sure what the format of the out put will be and hence passes an object
 * for display as the client sees fit. COntains the objects for use by a 
 * custom client.
 */

package events;

/**
 *
 * @author dd599
 */
import DPCCore.messages.*;

import java.util.EventObject;

public class MessageReceivedEvent extends EventObject {
    private String EventType = "";
    private String EventNotificaiton = "";
    private DPCMessage message;
    private DPCCore.messages.DPCError error;
    private SendMessage sendMSG;
    private StatusOfChatGroup statusOfChatGroup;
    private DPCMasterChatList MasterChatList;
    
    	    
    public MessageReceivedEvent(Object source, String sEvtType, DPCMessage msg, SendMessage sm) {
	super(source);
	this.EventType = sEvtType;
	this.message = msg;
	this.sendMSG = sm;
    }
    
    public MessageReceivedEvent(Object source, String sEvtType, StatusOfChatGroup st) {
	super(source);
	this.EventType = sEvtType;
	this.statusOfChatGroup = st;
    }
    
    public MessageReceivedEvent(Object source, String sEvtType, DPCMessage msg, String notification) {
	super(source);
	this.EventType = sEvtType;
	this.message = msg;
	this.EventNotificaiton = notification;
    }
    public MessageReceivedEvent(Object source, String sEvtType, DPCCore.messages.DPCError err) {
	super(source);
	this.EventType = sEvtType;
	this.error = err;
    }
    public MessageReceivedEvent(Object source, String sEvtType, DPCCore.messages.DPCMasterChatList mcl) {
	super(source);
	this.EventType = sEvtType;
	this.MasterChatList = mcl;
    }
    public String getEventNotification() { return this.EventNotificaiton; }
    public String getEventType() { return this.EventType; }
    public DPCMessage getMessage() { return this.message; }
    public SendMessage getSendMessage() { return this.sendMSG; }    
    public StatusOfChatGroup getStatusOfChatGroup() { return this.statusOfChatGroup; }
        public DPCError getError() { return this.error; }
                public DPCMasterChatList getMasterChatList() { return this.MasterChatList; }
               // public DPCMasterChatList getMasterChatList() { return this.MasterChatList; }
}
