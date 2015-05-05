/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.InetAddress;

/**
 *
 * @author dd599
 */

import java.net.*;
import java.io.*;

public class MulticastServer extends Thread {
    //Documentation: "A multicast group is specified by a class D IP address and by a standard UDP port number.
    // Class D IP addresses are in the range 224.0.0.0 to 239.255.255.255, inclusive. The address 224.0.0.0 is reserved and should not be used."
    // So this is only for a local network broadcast. I was looking for a way to get a list of IP addresses and send to all of them.
    private final String multicastGroup = "230.0.0.1"; // Special multicast address
    private final int multicastPost = 4567;
    public MulticastServer() {
	
    }
    
    public void run() {
	try{	    
	    MulticastSocket multicastSocket = new MulticastSocket(multicastPost);
	    multicastSocket.setSoTimeout(30000); // 30 second timeout
	    multicastSocket.joinGroup(InetAddress.getByName(multicastGroup));
	    byte[] data;
	    DatagramPacket packet;
	    while(true) {
		try{
		    data = new byte[8000];
		    packet = new DatagramPacket(data, data.length);
		    multicastSocket.receive(packet);
		    
		    String sMulticastMessage = new String(packet.getData()); // the data 
		    InetAddress messageSource = packet.getAddress(); // the source of the sender
		    System.out.println("We got a multicast message: "+ sMulticastMessage);
		}
		catch(Exception e) {}
		
		try{Thread.sleep(5000);} catch(Exception eeee){}
	    }
	}
	catch(Exception ee) {
	    ee.printStackTrace();
	}
    }
}
