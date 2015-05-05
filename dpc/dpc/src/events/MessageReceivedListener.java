package events;

/**
 *
 * @author dd599
 */
import java.util.EventListener;

public interface MessageReceivedListener extends EventListener {
    public void messageReceivedEventOccurred(MessageReceivedEvent event);
}
