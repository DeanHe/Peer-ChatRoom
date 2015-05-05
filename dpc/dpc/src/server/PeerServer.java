package server;

/**
 * This is a test class that establishes the threads that listen for client connections from clients
 * 
 * @author dd599
 */

import java.net.*;
import java.io.*;

public class PeerServer extends Thread {    
    protected Socket clientSocket;
 
 public PeerServer ()
   { }

 public void run()
   {
    try{
	ServerSocket serverSocket = new ServerSocket(7890);
    
        System.out.println ("Peer Socket Created");
	System.out.println ("New peer Thread Started");

	try { 
	    while(true) {
		System.out.println("Here 1.1");
		clientSocket = serverSocket.accept();
		System.out.println("Here 1.2");

		InputStream in = clientSocket.getInputStream();
		System.out.println("Here 1.3");
		StringBuffer data = new StringBuffer();
		System.out.println("Here 1.4");
		InetAddress source = clientSocket.getInetAddress();
		System.out.println("Here 1.5 from: "+source.getHostAddress());
		int c;
		while((c=in.read())!=-1) data.append((char) c);
		clientSocket.close();
		System.out.println("you got a message: "+data.toString());
	    }
	} 
	catch (IOException e) { 
	     System.err.println("Problem !");
	     System.exit(1); 
	}
    }
    catch(Exception eee) {eee.printStackTrace();}
    }   
}
