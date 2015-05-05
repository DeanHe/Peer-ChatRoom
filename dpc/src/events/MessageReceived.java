/**
 * MessageReceived.java
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * @date May 13, 2013
 * 
 * The DPC protocol uses an event trigger to update output. The protocol library 
 * cannot be sure what the format of the out put will be and hence passes an object
 * for display as the client sees fit. This class created the trigger.
 */

package events;

import javax.swing.event.EventListenerList;

/**
 *
 * @author dd599
 */

public class MessageReceived {
    protected EventListenerList listenerList = new EventListenerList();

    public void addMessageReceivedListener(MessageReceivedListener listener) {
	listenerList.add(MessageReceivedListener.class, listener);
    }
    public void removeMyEventListener(MessageReceivedListener listener) {
      listenerList.remove(MessageReceivedListener.class, listener);
    }
    public void fireMyEvent(MessageReceivedEvent evt) {
      Object[] listeners = listenerList.getListenerList();
      for (int i = 0; i < listeners.length; i = i+2) {
	if (listeners[i] == MessageReceivedListener.class) {
	    ((MessageReceivedListener) listeners[i+1]).messageReceivedEventOccurred(evt);
	}
      }
    }
}
