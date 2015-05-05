package client;

/**
 * @author Dimitar Dimitrov - Group 5
 * @date May 13, 2013

This is the main class of the Client of the Dragon Peer Chat Application
 */

//import components.test1;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import DPCCore.messages.ConnectToChatGroup; 

import DPCCore.*;
import DPCCore.messages.SendMessage;
import events.*;
import java.net.InetAddress;

import java.util.HashMap;
//import dpc.DCPCore.*;


public class MainScreen extends JPanel implements ActionListener, ListSelectionListener {
    private static JFrame mainFrame;
    public HashMap Peer_IPs = new HashMap(); // Contains <Peer Name> <PeerObject>
    public HashMap Peer_TextAreas = new HashMap(); // Contains <Peer name> <JTextArea> 
    public HashMap Peer_Lists = new HashMap();
    JTabbedPane tabbedPane;
    //DefaultListModel listModel;
    DefaultListModel chatRooms;
  public String Nick_CURRENT = "";
    public String sServerIP = "127.0.0.1";
    public String External_IP = "127.0.0.1";

    public int IP_Port = 1975;
    private DPCInstance globalDPCInstance = null;
    JTextArea testArea;
        public static boolean debug = false;

    public MainScreen(DPCCore.DPCInstance d) {

        super(new GridLayout());
	        try{
            sServerIP = InetAddress.getLocalHost().getHostAddress();
    External_IP = InetAddress.getLocalHost().getHostAddress();
        }catch(Exception e){}
	globalDPCInstance = d;
	
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        JComponent chatroom1 = makeTextPanel("DPC ChatRoom List", d);
        tabbedPane.addTab("DPC ChatRoom List", chatroom1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        add(tabbedPane);
    }

    public static void displayMainScreen(DPCCore.DPCInstance d)//DPCCore.DPCInstance d){ 
    {
        if (d!=null)
        mainFrame = new JFrame("Drexel Peer Chat (DPC) - " + d.getNick());
        else mainFrame = new JFrame("Drexel Peer Chat (DPC)");

        mainFrame.setPreferredSize(new Dimension(700, 600));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.add(new MainScreen(d), BorderLayout.SOUTH);
        mainFrame.setResizable(false);

        //Display the window.
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {
        // Events go here
    }

    /**
     * The main panel of the DPC Chat
     *
     * @param text
     * @param d
     * @return
     */
    protected JComponent makeTextPanel(String text, DPCCore.DPCInstance d) {
        /**
         * The main purpose of AllPeers hashtable is to provide a translation table between an IP address and the
         * java JTextArea component where the peer message is going to appear.
         * We can also use this hashmap to look for the tab that we have for this peer
         */
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setName(text);
       String test = javax.swing.JOptionPane.showInputDialog("Press enter to bypass entry.");
       if (d==null||d.getNick()==null||!"".equals(test))
        {
            this.Nick_CURRENT = javax.swing.JOptionPane.showInputDialog("Enter your NICK");
            sServerIP = javax.swing.JOptionPane.showInputDialog("Enter the IP address of the DPC Chat Server",sServerIP);
            External_IP = javax.swing.JOptionPane.showInputDialog("Enter your EXTERNAL IP", External_IP);
            String sP = javax.swing.JOptionPane.showInputDialog("Enter PORT number", ""+IP_Port);
            try {
                IP_Port = Integer.parseInt(sP);
            }
            catch(Exception ee) {
                System.out.println(ee.getMessage());
            }
        }
        else
        {
            this.Nick_CURRENT = d.getNick();//javax.swing.JOptionPane.showInputDialog("Enter your NICK");
            sServerIP = d.getMasterChatServer().IPv4;//, IP_Port)javax.swing.JOptionPane.showInputDialog("Enter the IP address of the DPC Chat Server",sServerIP);
            External_IP = d.inBox.getIP();//javax.swing.JOptionPane.showInputDialog("Enter your EXTERNAL IP", External_IP);
            IP_Port = d.inBox.getPort();//javax.swing.JOptionPane.showInputDialog("Enter PORT number", ""+IP_Port);
        
        }
        System.out.println(sServerIP);
        
        if (d==null)
            d=new DPCCore.DPCInstance(IP_Port, Nick_CURRENT);
        
        //JTextField messageEntryField = new JTextField(20);
        JTextArea messageEntryField = new JTextArea();
        //messageEntryField.addActionListener(this);

        JTextArea chatTextArea = new JTextArea(5, 50);
        chatTextArea.setEditable(false);
        chatTextArea.setLineWrap(true);
        chatTextArea.setWrapStyleWord(true);
        chatTextArea.append("Welcome to Dragon Peer Chat, " + d.getNick() + "\n\n"
		+ "Please select one of the available CHAT GROUPS on the right");
	testArea = chatTextArea; // FOR TESTING
	MessageReceived msgRcv = new MessageReceived();
	msgRcv.addMessageReceivedListener(new MessageReceivedListener() {
	    public void messageReceivedEventOccurred(MessageReceivedEvent evt) {
		processEvent(evt);
	    }
	});
        
	//d.setNick(Nick_CURRENT);
	d.setPublicIO(msgRcv);
        //d.setPublicIO(new StreamDirector(chatTextArea));
	//d.addPublicIO(new StreamDirector(chatTextArea), this.Nick_CURRENT);
        //d.setMainScreen(this);

        PeerObject mainChatroom = new PeerObject("localhost", "", "");
        addKeyListener(chatTextArea, messageEntryField, d, "");

        JScrollPane messageEntryFieldScrollPane = new JScrollPane(messageEntryField);
        JScrollPane chatAreaScrollPane = new JScrollPane(chatTextArea);

        //chatTextArea.
        chatRooms = new DefaultListModel();
	chatRooms.addElement("Chat Room 1");
	chatRooms.addElement("Chat Room 2");
	chatRooms.addElement("Chat Room 3");
	chatRooms.addElement("Chat Room 4");
	
       // addPeerToChatroom("Dimitar", "127.0.0.1", "");
       // addPeerToChatroom("Georgi", "127.0.0.1", "");
       // addPeerToChatroom("Andrew", "127.0.0.1", "");
       // addPeerToChatroom("Tengda", "127.0.0.1", "");

        JList buddyList = new JList(chatRooms);
        buddyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        buddyList.setSelectedIndex(0);
        buddyList.addListSelectionListener(this);
        buddyList.setVisibleRowCount(5);

        addMouseClicked(buddyList, d, 0, chatRooms);

        JScrollPane buddyListScrollPane = new JScrollPane(buddyList);
        buddyListScrollPane.setSize(new Dimension(15, panel.getHeight()));
	
        //Add Components to this panel.	
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 390;
        panel.add(chatAreaScrollPane, c);
	
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.ipady = 380;        
	panel.add(buddyListScrollPane, c);
	
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 45;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 2;
        panel.add(messageEntryFieldScrollPane, c);

        Peer_TextAreas.put("MainChatrooms", chatTextArea);
        Peer_IPs.put("MainChatrooms", mainChatroom);
	Peer_Lists.put("MainChatrooms", chatRooms);

        return panel;
    }
    
    protected JComponent makeChatroomPanel(String sChatroom, DPCInstance d) {
	JPanel chatRoom = new JPanel(new GridBagLayout());
        chatRoom.setOpaque(true);
        chatRoom.setName(sChatroom);
	
        JTextArea messageEntryField = new JTextArea();
        
        JTextArea chatTextArea = new JTextArea(5, 50);
        chatTextArea.setEditable(false);
        chatTextArea.setLineWrap(true);
        chatTextArea.setWrapStyleWord(true);
        chatTextArea.append("You have successfully joined chatroom " + sChatroom +", " + d.getNick());

        //d.addPublicIO(new StreamDirector(chatTextArea), this.Nick_CURRENT);
        //d.setMainScreen(this);

        PeerObject mainChatroom = new PeerObject("localhost", "", "");
        addKeyListener(chatTextArea, messageEntryField, d, "CHATGROUP_"+sChatroom);

        JScrollPane messageEntryFieldScrollPane = new JScrollPane(messageEntryField);
        JScrollPane chatAreaScrollPane = new JScrollPane(chatTextArea);

        //chatTextArea.
        DefaultListModel listModel = new DefaultListModel();	
        addPeerToChatroom("Dimitar", "127.0.0.1", "", listModel);
        addPeerToChatroom("Georgi", "127.0.0.1", "", listModel);
        addPeerToChatroom("Andrew", "127.0.0.1", "", listModel);
        addPeerToChatroom("Tengda", "127.0.0.1", "", listModel);

        JList buddyList = new JList(listModel);
        buddyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        buddyList.setSelectedIndex(0);
        buddyList.addListSelectionListener(this);
        buddyList.setVisibleRowCount(5);

        addMouseClicked(buddyList, d, 1, listModel);

        JScrollPane buddyListScrollPane = new JScrollPane(buddyList);
        buddyListScrollPane.setSize(new Dimension(15, chatRoom.getHeight()));
	
        //Add Components to this panel.	
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 390;
        chatRoom.add(chatAreaScrollPane, c);
	
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.ipady = 380;        
	chatRoom.add(buddyListScrollPane, c);
	
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 45;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 2;
        chatRoom.add(messageEntryFieldScrollPane, c);

        Peer_TextAreas.put(sChatroom, chatTextArea);
        Peer_IPs.put(sChatroom, mainChatroom);
	Peer_Lists.put(sChatroom, listModel);

        return chatRoom;
    }

    protected JComponent makePrivateChatPanel(String sPeerName, DPCInstance d) {
        JPanel privateChatPanel = new JPanel(new GridBagLayout());

        JTextArea messageEntryField = new JTextArea();
        messageEntryField.setLineWrap(true);
        //messageEntryField.addActionListener(this);
        JScrollPane messageEntryFieldScrollPane = new JScrollPane(messageEntryField);

        JTextArea chatTextArea = new JTextArea(5, 50);
        chatTextArea.setEditable(false);
        chatTextArea.setLineWrap(true);
        chatTextArea.append("**Starting private chat session with " + sPeerName + " **");
        JScrollPane chatAreaScrollPane = new JScrollPane(chatTextArea);

        addKeyListener(chatTextArea, messageEntryField, d, sPeerName);

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 390;
        privateChatPanel.add(chatAreaScrollPane, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 45;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridx = 0;
        c.gridwidth = 0;
        c.gridy = 0;
        privateChatPanel.add(messageEntryFieldScrollPane, c);

        // Update the HashMap to include the new translation
        Peer_TextAreas.put(sPeerName, chatTextArea);

        return privateChatPanel;
    }

    private void addMouseClicked(JList buddyList, final DPCInstance d, final int isPeerList, final DefaultListModel listModel) {
        buddyList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getComponent() instanceof JList) {
                    JList list = (JList) evt.getSource();
                    if (evt.getClickCount() == 2) {
			int index = list.locationToIndex(evt.getPoint());
						
			switch(isPeerList) {
			    case 0: // This is a double-click within the main chatroom window
				String sChatRoomName = chatRooms.getElementAt(index).toString();
				//System.out.println(tabbedPane.getTitleAt(i));
				if (tabbedPane.indexOfTab(sChatRoomName) >= 0) {
				    // We have this tab already - we don't create it, but swith it to be the active one
				    tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(sChatRoomName));
				} else {
				    // create a new tab for private chat
				    createNewChatroomWindow(sChatRoomName, d);
				}
				break;
			    case 1: // This is a double-click within for a private peer chat
				String sPeerName = listModel.getElementAt(index).toString();
				//System.out.println(tabbedPane.getTitleAt(i));
				if (tabbedPane.indexOfTab(sPeerName) >= 0) {
				    // We have this tab already - we don't create it, but swith it to be the active one
				    tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(sPeerName));
				} else {
				    // create a new tab for private chat
				    createPrivateChat(sPeerName, d);
				}
				break;
			}                       
                    }
                }
            }
        });
    }
    
    private void addKeyListener(final JTextArea chatArea, final JTextArea userEntry, final DPCInstance dpcInstance, final String sPeerName) {
        try {
            userEntry.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                }

                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (userEntry.getText().trim().length() > 0) {
			    
                            switch (userEntry.getText().trim()) {
                                case "/ConnectToChatGroup":
                                    //DPCInstance.SendMessage("127.0.0.1", new ConnectToChatGroup());
				    chatArea.append("\n"+dpcInstance.getNick()+": "+userEntry.getText().trim());
                                    userEntry.setText("");
                                    break;
                                default:
				    if(sPeerName == null || sPeerName.equals("")) {
					//System.out.println("Sending group message");
					// IF there is no peer name then this is a main screen main room message
					DPCInstance.SendMessage("HAJ123", new SendMessage(userEntry.getText().trim()));
					chatArea.append("\n"+dpcInstance.getNick()+": "+userEntry.getText().trim());
				    }
				    else {
					if(sPeerName.startsWith("CHATGROUP_")) {
					    // This is a chatgroup message
					    //System.out.println("Sending group message");
					    // IF there is no peer name then this is a main screen main room message
					    DPCInstance.SendMessage("HAJ123", new SendMessage(userEntry.getText().trim()));
					    chatArea.append("\n"+dpcInstance.getNick()+": "+userEntry.getText().trim());
					}
					else {
					    // This is a peer message
					    //System.out.println("Sending private");
					    DPCInstance.SendMessage("HAJ123", new SendMessage(userEntry.getText().trim()));
					    chatArea.append("\n"+dpcInstance.getNick()+": "+userEntry.getText().trim());
					}
				    }
                                    userEntry.setText("");
                                    break;
                            }
                        }
                    }
                }

                public void keyTyped(KeyEvent e) {
                }
            });
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

        }
    }

    private void createNewChatroomWindow(String sChatroom, DPCInstance d) {
	JComponent chatRoomPanel = makeChatroomPanel(sChatroom, d);
	tabbedPane.insertTab(sChatroom, null, chatRoomPanel, sChatroom, tabbedPane.getTabCount());
	tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }
    
    private void createPrivateChat(String peerName, DPCInstance d) {
        JComponent privateChat = makePrivateChatPanel(peerName, d);
        tabbedPane.insertTab(peerName, null, privateChat, peerName, tabbedPane.getTabCount());
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public void addPeerToChatroom(String sPeer, String sIP, String sPort, DefaultListModel listModel) {
        try {
            PeerObject peer = new PeerObject(sPeer, sIP, sPort);
            listModel.addElement(peer.getPeerName());
            Peer_IPs.put(peer.getPeerName(), peer);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
    
    public void updateBuddyList(String sChatroom, DPCCore.messages.StatusOfChatGroup statusOfChatGroup) {
	if(Peer_Lists.containsKey(sChatroom)) {
	    DefaultListModel list = (DefaultListModel) Peer_Lists.get(sChatroom);
	    list.clear();
	    // Then add the updated buddy list here
	    for(int i=0; i<statusOfChatGroup.Contacts.size(); i++) {
		Origin origin = (Origin)statusOfChatGroup.Contacts.get(i);
		addPeerToChatroom(origin.Nick, origin.IPv4, ""+origin.Port, list);
	    }
	}
    }

    public void removePeerFromChatroom(String sPeer, String sIP, String sPort, DefaultListModel listModel) {
        try {
            if (listModel.indexOf(sPeer) >= 0) {
                listModel.removeElementAt(listModel.indexOf(sPeer));
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    public void displayPeerMessage(String sPeer, String sFrom, String sMessage) {
        System.out.println("Looking for : " + sPeer + " from: " + sFrom + " with message: " + sMessage);
        if (Peer_TextAreas.containsKey(sPeer)) {
            JTextArea ptx = (JTextArea) Peer_TextAreas.get(sPeer);
            ptx.append("\n" + sFrom + ": " + sMessage);
        }
    }

    private void processEvent(MessageReceivedEvent evt) {
	JTextArea textArea = testArea; // Default
	switch(evt.getEventType()) {
	    case DPCCore.messages.MessageTypes.UPDATE_MASTER_CHAT_LIST: break;
	    case DPCCore.messages.MessageTypes.SEND_MASTER_CHAT_LIST: break;
	    case DPCCore.messages.MessageTypes.STATUS_OF_CHAT_GROUP: 
<<<<<<< .mine
                if(!Peer_TextAreas.containsKey(evt.getStatusOfChatGroup().Title)) {
                    createNewChatroomWindow(evt.getStatusOfChatGroup().Title, this.globalDPCInstance);

		}
                    textArea = (JTextArea)Peer_TextAreas.get(DPCInstance.getGroup(evt.getStatusOfChatGroup().ThreadID).Title);
		    textArea.append("\n\n"+evt.getStatusOfChatGroup().Title+"\n\n"+evt.getStatusOfChatGroup().Description+"\n\n"); 
                    
                    
                    break;
                    
                
                
	    
            case DPCCore.messages.MessageTypes.SEND_MESSAGE:
		
//		if(Peer_TextAreas.containsKey(evt.getMessage().Origin.Nick)) { 
//		    textArea = (JTextArea)Peer_TextAreas.get(evt.getMessage().Origin.Nick);
//		    textArea.append("\n"+evt.getMessage().Origin.Nick+": "+evt.getSendMessage().Message);
//		}
                if(Peer_TextAreas.containsKey(DPCInstance.getGroup(evt.getMessage().Destination.ThreadID).Title)) { 
		    textArea = (JTextArea)Peer_TextAreas.get(DPCInstance.getGroup(evt.getMessage().Destination.ThreadID).Title);
=======
		// Update the buddy list
		updateBuddyList(evt.getStatusOfChatGroup().ThreadID, evt.getStatusOfChatGroup()); 
		//textArea.append("\n"+evt.getStatusOfChatGroup().WelcomeMessage); 
		break;
	    case DPCCore.messages.MessageTypes.SEND_MESSAGE:
		if(evt.getMessage().Origin.Nick == Nick_CURRENT) break; // we don't want to display messages from ourselves to ourselves
		if(Peer_TextAreas.containsKey(evt.getMessage().Origin.Nick)) { 
		    textArea = (JTextArea)Peer_TextAreas.get(evt.getMessage().Origin.Nick);
>>>>>>> .r96
		    textArea.append("\n"+evt.getMessage().Origin.Nick+": "+evt.getSendMessage().Message);
		}
		else {
		    if(evt.getMessage().Destination.ThreadID != null && !evt.getMessage().Destination.ThreadID.equals("")) {
			// We have a peer message for a peer that we don't have a TAB for. So we need to create one
			createPrivateChat(evt.getMessage().Origin.Nick, globalDPCInstance);
			textArea = (JTextArea)Peer_TextAreas.get(evt.getMessage().Origin.Nick);
			textArea.append("\n"+evt.getMessage().Origin.Nick+": "+evt.getSendMessage().Message);
		    }
		    else {
			textArea.append("\n"+evt.getMessage().Origin.Nick+": "+evt.getSendMessage().Message);
		    }
		}
		 break;
	    case DPCCore.messages.MessageTypes.PING: 
		if(Peer_TextAreas.containsKey(evt.getMessage().Origin.Nick)) { 
		    textArea = (JTextArea)Peer_TextAreas.get(evt.getMessage().Origin.Nick);
		    textArea.append("\n"+evt.getMessage().Origin.Nick+" --- "+evt.getEventNotification());
		}
		else {
		    textArea.append("\n"+evt.getMessage().Origin.Nick+" --- "+evt.getEventNotification()); 
		}
		break;
	    case DPCCore.messages.MessageTypes.PING_ACK: 
		if(Peer_TextAreas.containsKey(evt.getMessage().Origin.Nick)) { 
		    textArea = (JTextArea)Peer_TextAreas.get(evt.getMessage().Origin.Nick);
		    textArea.append("\n"+evt.getMessage().Origin.Nick+" --- "+evt.getEventNotification()); 
		}
		else {
		    textArea.append("\n"+evt.getMessage().Origin.Nick+" --- "+evt.getEventNotification()); 
		}
		break;
	    case DPCCore.messages.MessageTypes.APPOINT: break;
	    case DPCCore.messages.MessageTypes.APPOINT_ACK: break;
	    case DPCCore.messages.MessageTypes.CHANGE_KEY_REQUEST: break;
	    case DPCCore.messages.MessageTypes.CHANGE_KEY_RESPONSE: break;
	    case DPCCore.messages.MessageTypes.CONNECT_TO_CHAT_GROUP: break;
	    case DPCCore.messages.MessageTypes.DPC_MESSAGE: break;
	    
	    default:
		textArea.append("\nEvent triggered "+evt.getMessage().Origin.Nick+" with type: "+evt.getEventType());
		System.out.println("Event triggered "+evt.getMessage().Origin.Nick+" with type: "+evt.getEventType()); break;
	}
    }

    public static void main(String[] args) {
        if (args.length>0) debug = true;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (debug==true)
                {
                try {
                    displayMainScreen(new DPCCore.DPCInstance(1964, "Wonder Man"));
                } catch (Exception e1) {
			
                    try {
                        displayMainScreen(new DPCCore.DPCInstance(1871, "Octavius Catto"));
                    } catch (Exception e2) {
                        displayMainScreen(new DPCCore.DPCInstance(1916, "James Connally"));
                    }
                }
                }
                else{displayMainScreen(null);}
                //, ((JScrollPane)((JPanel)((MainScreen)mainFrame.getContentPane().getComponents()[0]).tabbedPane.getComponentAt(0)).getComponent(0)).getViewport().getView()));
                try {
                    //DPCCore.DPCServer s = new DPCCore.DPCServer("127.0.0.1",1976, ((JScrollPane)((JPanel)((MainScreen)mainFrame.getContentPane().getComponents()[0]).tabbedPane.getComponentAt(0)).getComponent(0)).getViewport().getView()) ;
                    //DPCCore.DPCInstance s = new DPCCore.DPCInstance("127.0.0.1",1976, ((JScrollPane)((JPanel)((MainScreen)mainFrame.getContentPane().getComponents()[0]).tabbedPane.getComponentAt(0)).getComponent(0)).getViewport().getView()) ;
                    //new Thread(s).start();
                    //s.start();
                } catch (Exception e) {
                    String a = e.toString();
                    e.printStackTrace();
                }
            /*            Object obj = null;
        String json = "";
        
                        try{
         //java.net.Socket s = new java.net.Socket("127.0.0.1",1975);
          // s. 
                   
                   java.net.ServerSocket serverSocket = new java.net.ServerSocket(1212);

        while(true) {
            Socket connectionSocket = serverSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            //clientSentence = inFromClient.readLine();
            //capitalizedSentence = clientSentence.toUpperCase() + '\n';
            //outToClient.writeBytes(capitalizedSentence);
                            Map graph = JsonReader.jsonToMaps(inFromClient.readLine());
          json = JsonWriter.objectToJson(graph);
        }

            //JsonReader jr = new JsonReader(InputStream, true);
            //Map map = (Map) jr.readObject()
          //json = json+"XXX";
        }catch(Exception e){}*/


            }
        });
    }
}
