/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author dd599
*/

import java.io.*;
import java.net.*;

public class client {
    public client(){
	doWork();	
    }
    
    public void doWork(){
	try{
	    System.out.println("Hello - start typing");
	    //java.util.HashMap peers = new java.util.HashMap<>();
	    server.PeerServer ps = new server.PeerServer();
	    ps.start();
	    //ps.setupPeerServer();
	        
	    
	    server.MulticastServer ms = new server.MulticastServer();
	    ms.start();
	        
	    BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
	    String line = "";
	    while((line = buf.readLine()) != null) {
		System.out.println("You entered: "+line);
	    
		if(line.startsWith("broadcast")) {
		    try{
			    byte[] data = line.getBytes();
			    DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName("230.0.0.1"), 4567);
			    MulticastSocket multicastSocket = new MulticastSocket();
			    multicastSocket.send(datagramPacket);
		    }
		    catch(IOException ie){
			    System.err.println(ie);
		    }
		}
		if(line.startsWith("message")) {
		    OutputStreamWriter out = null;
		    try{
			Socket theSocket = new Socket(InetAddress.getByName("192.168.29.128"),7890);
			OutputStream raw = theSocket.getOutputStream();
			OutputStream buffered = new BufferedOutputStream(raw);
			out = new OutputStreamWriter(buffered, "ASCII");
			out.write(line);
			out.flush();
			out.close();
			theSocket.close();
		    }
		    catch(Exception eeee){eeee.printStackTrace();}
		}
		if(line.startsWith("peers")) {
		    
		}
	    }
	}
	catch(Exception ee) {
	    ee.printStackTrace();
	}
    }
    
    public static void main(String[] args) {
	new client();
    }
}
