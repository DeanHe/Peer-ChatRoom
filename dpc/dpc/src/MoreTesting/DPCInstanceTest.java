package MoreTesting;

import DPCCore.DPCInstance;
import DPCCore.messages.Destination;
import DPCCore.messages.SendMessage;

/**
 *  @author Georgi Simeonov
 *  The goal of this class is to test messages. In order to do that, look at the MainScreen.java line 298:
 *  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
    public void run() {
    try {
        displayMainScreen(new DPCCore.DPCInstance(1975, "Wonder Man"));

    Get the port number that it is running 1975 (right above)  and send the message to that port and the ip address of the server.

    I run the MainScreen on one machine, which happened to have ip 192.168.1.12. Run the test below on a different machine.

    Result: I saw the message displayed in the chatroom window because they share the same thread - HAJ123.
 *
 */
public class DPCInstanceTest {
    public static void main(String[] args) {
        //5791 is the port that this instance is running on.
        DPCInstance dpcInstance = new DPCInstance(5791, "Octavius Catto");
        // 192.168.1.12 is the ip of the machine that has MainScreen running and 1975 is the IP that it is running on.
        dpcInstance.SendMessage(new Destination("192.168.1.12", "", 1975, "HAJ123"), new SendMessage("Sample Message 1."));
    }
}
