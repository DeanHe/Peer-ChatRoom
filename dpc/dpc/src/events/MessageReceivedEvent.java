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
    private SendMessage sendMSG;
    private StatusOfChatGroup statusOfChatGroup;
    	    
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
    
    public String getEventNotification() { return this.EventNotificaiton; }
    public String getEventType() { return this.EventType; }
    public DPCMessage getMessage() { return this.message; }
    public SendMessage getSendMessage() { return this.sendMSG; }    
    public StatusOfChatGroup getStatusOfChatGroup() { return this.statusOfChatGroup; }
}
