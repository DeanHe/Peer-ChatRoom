/**
 * MessageReceivedListener.java
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * @date May 13, 2013
 * 
 * The DPC protocol uses an event trigger to update output. The protocol library 
 * cannot be sure what the format of the out put will be and hence passes an object
 * for display as the client sees fit. This is the core listener for the trigger.
 */

package events;


import java.util.EventListener;

public interface MessageReceivedListener extends EventListener {
    public void messageReceivedEventOccurred(MessageReceivedEvent event);
}
