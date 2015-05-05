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
