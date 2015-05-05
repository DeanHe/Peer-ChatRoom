package client;

/**
 * MainScreen.java
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * @date May 13, 2013
 * 
 * This class is the main UI component of the application that interfaces with the DPC Protocol
 * This is the main class of the Client of the Dragon Peer Chat Application
 * It creates a Tabbed Pane that allows users to navigate the application, join and leave chatgroups and create private messages to peers
 * IT takes 0 or 1 parameter. With 1 parameter, the application will use some of the hard-coded examples. With 0 parameters the application will 
 * ask the user for inputs (such as what the server IP address is, what port to use, etc,)
It fulfills the UI requirement.
 */

// Seciton to import all necessary libraries and dependencies
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.security.PublicKey;
import javax.crypto.SealedObject;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import DPCCore.messages.ConnectToChatGroup; 

import DPCCore.*;
import static DPCCore.DPCInstance.selfElect;
import DPCCore.messages.DPCChatGroup;
import DPCCore.messages.SendMessage;
import DPCCore.messages.StatusOfChatGroup;
import events.*;
import java.net.InetAddress;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class MainScreen extends JPanel implements ActionListener, ListSelectionListener {
    // Declaration of global variables 
    private static JFrame mainFrame;
    public HashMap Peer_IPs = new HashMap(); // Contains <Peer Name> <PeerObject>
    public HashMap Peer_TextAreas = new HashMap(); // Contains <Peer name> <JTextArea> 
    public HashMap Peer_Lists = new HashMap();
    public Hashtable chatGroups = new Hashtable();
    JTabbedPane tabbedPane; // main tabbed pane
    //DefaultListModel listModel;
    DefaultListModel chatRooms;
    public String Nick_CURRENT = ""; // variable initialization for the Nick
    public String sServerIP = "127.0.0.1"; // initilize to local loopback
    public String External_IP = "127.0.0.1"; // initilize to local loopback

    public int IP_Port = 1975;
    private DPCInstance globalDPCInstance = null; // creates a global instance of the DPCInstance object (this initializes it)
    JTextArea testArea;
        public static boolean debug = false; // Debugging flag true/false

	/**
	 * Constructor of the UI
	 * @param d - the DPCInstance instance that was started for this application thread
	 */
    public MainScreen(DPCCore.DPCInstance d) {
	// Everything is arranged on a grid layout basis
        super(new GridLayout());
	        try{
            sServerIP = InetAddress.getLocalHost().getHostAddress(); // Get the local host IP address
	    External_IP = InetAddress.getLocalHost().getHostAddress(); // Initialize the variable to be the local host IP address
        }catch(Exception e){}
	globalDPCInstance = d; // Associate the instance
	
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        JComponent chatroom1 = makeTextPanel("DPC ChatRoom List", d); // Set the title of the window
        tabbedPane.addTab("DPC ChatRoom List", chatroom1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        add(tabbedPane);
    }

    /**
     * This displays the main screen of the application with all available chatgroups
     * @param d - The DPCInstance that was started by the UI
     */
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

    /**
     * Action listener for action performed - included here because we Implement ActionListener
     * @param evt 
     */
    public void actionPerformed(ActionEvent evt) {
        // Events go here
    }

    /**
     * The main panel of the DPC Chat
     *
     * @param text - the title of the Tab
     * @param d - The DPC instance
     * @return The components that are to be rendered on the screen (within the JTabbedPane)
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
       String test = javax.swing.JOptionPane.showInputDialog("Press enter to bypass entry."); // This provides a short-cut through the applicaiton without having to ask the user for too much input
       if (d==null||d.getNick()==null||!"".equals(test))
        {
	    // Otherwise we want to collect al of this information from the user
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
            d=null;
        }
        else
        {
            this.Nick_CURRENT = d.getNick();//javax.swing.JOptionPane.showInputDialog("Enter your NICK");
            sServerIP = d.getMasterChatServer().IPv4;//, IP_Port)javax.swing.JOptionPane.showInputDialog("Enter the IP address of the DPC Chat Server",sServerIP);
            External_IP = d.inBox.getIP();//javax.swing.JOptionPane.showInputDialog("Enter your EXTERNAL IP", External_IP);
            IP_Port = d.inBox.getPort();//javax.swing.JOptionPane.showInputDialog("Enter PORT number", ""+IP_Port);
        
        }
        System.out.println(sServerIP); // for debugging purposes we print out the IP address
        
        if (d==null||d.getNick()==null) // if we don't have an instance running of the DPCInstnace theny we want to get one running
        {            
            d=new DPCCore.DPCInstance(IP_Port, Nick_CURRENT);
            d.setMasterChatServer(sServerIP, DPCCore.DPCConstants.MASTER_SERVER_PORT);
        }
        DPCInstance.askForMasterChatList(); // Reuqest the master chatlist - all of the currently available chatgroups
        //JTextField messageEntryField = new JTextField(20);
        JTextArea messageEntryField = new JTextArea();
        //messageEntryField.addActionListener(this);

	/**
	 * Create the chat areas for entry and user/Group messages
	 */
        JTextArea chatTextArea = new JTextArea(5, 50);
        chatTextArea.setEditable(false);
        chatTextArea.setLineWrap(true);
        chatTextArea.setWrapStyleWord(true);
        chatTextArea.append("Welcome to Dragon Peer Chat, " + d.getNick() + "\n\n"
		+ "Please select one of the available CHAT GROUPS on the right"); // A nice welcome message
	testArea = chatTextArea; // FOR TESTING
	/**
	 * We need to add an event listener to the input box so that we can capture messages that have arrived
	 */
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

	// We use PeerObjects to store some peer-specific informaiton that can be easily and quickly refernced
        PeerObject mainChatroom = new PeerObject("localhost", "", "");
        addKeyListener(chatTextArea, messageEntryField, d, ""); // And we add a key-listener to the user text-entry area

        JScrollPane messageEntryFieldScrollPane = new JScrollPane(messageEntryField);
        JScrollPane chatAreaScrollPane = new JScrollPane(chatTextArea);

        //chatTextArea.
        chatRooms = new DefaultListModel();
	chatRooms.addElement("Fetching chat groups...."); // we start by requesting the chatgroups from the server

	
       // addPeerToChatroom("Dimitar", "127.0.0.1", "");
       // addPeerToChatroom("Georgi", "127.0.0.1", "");
       // addPeerToChatroom("Andrew", "127.0.0.1", "");
       // addPeerToChatroom("Tengda", "127.0.0.1", "");

	/**
	 * Create the chatroom list
	 */
        JList buddyList = new JList(chatRooms); 
        buddyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        buddyList.setSelectedIndex(0);
        buddyList.addListSelectionListener(this);
        buddyList.setVisibleRowCount(5);

        addMouseClicked(buddyList, d, 0, chatRooms); // We want to monitor for mouse-click events in the list so we add a mouse listener. Only responds to double-clicks

        JScrollPane buddyListScrollPane = new JScrollPane(buddyList);
        buddyListScrollPane.setSize(new Dimension(15, panel.getHeight()));
	
        //Add Components to this panel and set their alignment
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

	// We want to record the components that we just created and the tabs that they are associated with. This allows us to write output to the right java swing component
        Peer_TextAreas.put("MainChatrooms", chatTextArea);
        Peer_IPs.put("MainChatrooms", mainChatroom);
	Peer_Lists.put("MainChatrooms", chatRooms);

        return panel;
    }
    
    /**
     * Makes the chatroom panel for an individual chatroom
     * @param sChatroom - the name of the chatgroup (and the tab name)
     * @param d - DPCInstance 
     * @return Returns the Java swing components that are to be displayed on the screen
     */
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
        addPeerToChatroom("Retrieving List:", "127.0.0.1", "", listModel);
        //addPeerToChatroom("Georgi", "127.0.0.1", "", listModel);
        //addPeerToChatroom("Andrew", "127.0.0.1", "", listModel);
        //addPeerToChatroom("Tengda", "127.0.0.1", "", listModel);

        JList buddyList = new JList(listModel); // Create a list of the available ChatGroups
        buddyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        buddyList.setSelectedIndex(0);
        buddyList.addListSelectionListener(this);
        buddyList.setVisibleRowCount(5);

        addMouseClicked(buddyList, d, 1, listModel); // Add a mouse listener to this component as well

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

    /**
     * This creates a private Peer chat screen (no list of chatgroups or peers is visible on this screen)
     * @param sPeerName - the Peer name
     * @param d - the DPCInstance
     * @return Returns the Java swing components that are to be displayed on the screen
     */
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

    /**
     * The Mouse clicked listener listens for mouse clicks within the Buddy and ChatGroup selection lists
     * @param buddyList - It takes the list component as a param
     * @param d - the DPC Instance
     * @param isPeerList - An indicator whether this list contains peers or chatgroups
     * @param listModel - the list model component itself
     */
    private void addMouseClicked(JList buddyList, final DPCInstance d, final int isPeerList, final DefaultListModel listModel) {
        buddyList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getComponent() instanceof JList) { // make sure that this is indeed a list component
                    JList list = (JList) evt.getSource();
                    if (evt.getClickCount() == 2) { // Make sure it's a dobule-click
			int index = list.locationToIndex(evt.getPoint()); // get the elements in the component and the selected index
						
			switch(isPeerList) {
			    case 0: // This is a double-click within the main chatroom window
				String sChatRoomName = chatRooms.getElementAt(index).toString();
				//System.out.println(tabbedPane.getTitleAt(i));
				if (tabbedPane.indexOfTab(sChatRoomName) >= 0) {
				    // We have this tab already - we don't create it, but swith it to be the active one
				    tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(sChatRoomName));
				} else {
				    // create a new tab for private chat
				    // createNewChatroomWindow(sChatRoomName, d);
                                    // Issue a protocol state change request to join the chatroom
                                    DPCInstance.joinChatGroup((DPCChatGroup)chatGroups.get(chatRooms.getElementAt(index).toString()));
				    //DPCCore.Destination d=new DPCCore.Destination("127.0.0.1", "", 1964, "HAJ123");
                                    //DPCInstance.SendMessage(d, new ConnectToChatGroup("HAJ123","")); 
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
    
    /**
     * Create a key listener for the user text entry areas
     * @param chatArea - the JTextArea where the messages should be displayed
     * @param userEntry - the JTextArea component where the actual message entry took place
     * @param dpcInstance - DPCInstance
     * @param sPeerName  - the name of the tab (peer or ChatGroup)
     */
    private void addKeyListener(final JTextArea chatArea, final JTextArea userEntry, final DPCInstance dpcInstance, final String sPeerName) {
        try {
            userEntry.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                }

                public void keyReleased(KeyEvent e) { // Record the key entry event
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) { // make sure that the user pressed the "Enter" (Return) key
                        if (userEntry.getText().trim().length() > 0) {
			    
                            String s = ""; 
                            String [] args = null;
    
			    // Do some string parsing
                            try{
                            s = userEntry.getText().trim().substring(0, userEntry.getText().trim().indexOf(" ")).trim();
                            args = userEntry.getText().trim().split(" ");
                            }
                            catch(Exception ex){s=userEntry.getText().trim();}
                            
			    // Then check to see if the message is of a certian type (i.e. that the user has 
                            switch (s.toLowerCase()){//(userEntry.getText().trim()) {
                                case "/connect": // a Connect to chatGroup message command was issued
                                    DPCCore.Destination d=new DPCCore.Destination("127.0.0.1", "", 1964, "HAJ123");
                                    DPCInstance.SendMessage(d, new ConnectToChatGroup("HAJ123",""));
				    chatArea.append("\n"+dpcInstance.getNick()+": "+userEntry.getText().trim());
                                    userEntry.setText("");
                                    break;
                                 case "/ping": // a Ping command was issued
                                     	    //if(d.Nick.equals("James Connally"))
                                            selfElect("HAJ123");
                                    //DPCInstance.setCallback(args[1], "Ping", new DPCCore.messages.Ping());
                                    break;
                                case "/create": //a Create ChatGroup command was issued
                                        DPCInstance.createChatGroup(args[1], args[2], args[3]);
                                    break;
                                default: // Otherwise, we have a message send command
				    if(sPeerName == null || sPeerName.equals("")) {
					System.out.println("Sending group message");
					// IF there is no peer name then this is a main screen main room message
					//DPCInstance.SendMessage("HAJ123", new SendMessage(userEntry.getText().trim()));
					//chatArea.append("\n"+dpcInstance.getNick()+": "+userEntry.getText().trim());
				    }
				    else {
					if(sPeerName.startsWith("CHATGROUP_")) {
					    // This is a chatgroup message
					    //System.out.println("Sending group message");
					    // IF there is no peer name then this is a main screen main room message
                                            String room = sPeerName.trim().substring(sPeerName.trim().indexOf("_")+1).trim();
                                            try {
                                            DPCChatGroup g = (DPCChatGroup)chatGroups.get(room);
					    // Sends a message to the Chatgroup
                                            DPCInstance.SendMessage(g.ThreadID, new SendMessage(userEntry.getText().trim()));
                                            } 
                                            catch(Exception e2){
						// If an exception is thrown then an update is sent to the master server
                                                DPCCore.messages.StatusOfChatGroup scg = (DPCCore.messages.StatusOfChatGroup)chatGroups.get(room);
                                                DPCInstance.SendMessage(scg.ThreadID, new SendMessage(userEntry.getText().trim()));
                                            }
					    
					    //chatArea.append("\n"+dpcInstance.getNick()+": "+userEntry.getText().trim());
					}
					else {
					    // This is a peer message
					    System.out.println("Sending private to "+sPeerName);
                        //translate the Nick to a destination

                        Origin sPeerOrigin = null;
                        Enumeration tmpChatGroups = chatGroups.elements();
                        while(tmpChatGroups.hasMoreElements()) {
                            StatusOfChatGroup stgroup = (StatusOfChatGroup) tmpChatGroups.nextElement();
                            Iterator<Origin> i = stgroup.getContacts().iterator();
                            while (i.hasNext()) {
                                Origin tmpOrig = i.next();
                                if (tmpOrig.Nick.equalsIgnoreCase(sPeerName)) {
                                    sPeerOrigin = tmpOrig;
                                }
                            }
                        }
                        if (sPeerOrigin != null) {
                            DPCInstance.SendMessage(
                                    new Destination(sPeerOrigin.IPv4,"",sPeerOrigin.Port,"", sPeerName),
                                    new SendMessage(userEntry.getText().trim()));
                            chatArea.append("\n"+dpcInstance.getNick()+": "+userEntry.getText().trim());

                        }
                        else {
                            System.out.println("Error! No Destination with Nick " + sPeerName);
                        }
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
    /**
     * This method is used to create the java components that go in the chatgroup window
     * @param sChatroom - the name of the tab
     * @param d - the DPCInstance
     */
    private void createNewChatroomWindow(String sChatroom, DPCInstance d) {
	JComponent chatRoomPanel = makeChatroomPanel(sChatroom, d);
	tabbedPane.insertTab(sChatroom, null, chatRoomPanel, sChatroom, tabbedPane.getTabCount());
	tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }
    
    /**
     * Creates the private chat window display components
     * @param peerName - the name of the tab
     * @param d - the DPCInstance
     */
    private void createPrivateChat(String peerName, DPCInstance d) {
        JComponent privateChat = makePrivateChatPanel(peerName, d);
        tabbedPane.insertTab(peerName, null, privateChat, peerName, tabbedPane.getTabCount());
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    /**
     * Adds a peer to the chatgroup list
     * @param sPeer - the peer name
     * @param sIP - their IP address
     * @param sPort - the peer port number
     * @param listModel - the list model java component where the peer should appear
     */
    public void addPeerToChatroom(String sPeer, String sIP, String sPort, DefaultListModel listModel) {
        try {
            PeerObject peer = new PeerObject(sPeer, sIP, sPort);
            listModel.addElement(peer.getPeerName());
            Peer_IPs.put(peer.getPeerName(), peer);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
    
    /**
     * Updates the buddy list
     * @param sChatroom - the name of the chatgroup
     * @param statusOfChatGroup  - the status of the chatgroup that contains the peers and their details
     */
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

    /**
     * Remove a peer from the chatgroup list when they have left
     * @param sPeer - remove the peer name
     * @param sIP - their IP
     * @param sPort - the peer Port number
     * @param listModel - the java list model component
     */
    public void removePeerFromChatroom(String sPeer, String sIP, String sPort, DefaultListModel listModel) {
        try {
            if (listModel.indexOf(sPeer) >= 0) {
                listModel.removeElementAt(listModel.indexOf(sPeer));
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    /**
     * display a message in the necessary tab and java component
     * @param sPeer - the peer name - used to identify what tab the message should go to
     * @param sFrom - the person who sent the message
     * @param sMessage - the actual message
     */
    public void displayPeerMessage(String sPeer, String sFrom, String sMessage) {
        System.out.println("Looking for : " + sPeer + " from: " + sFrom + " with message: " + sMessage);
        if (Peer_TextAreas.containsKey(sPeer)) {
            JTextArea ptx = (JTextArea) Peer_TextAreas.get(sPeer);
            ptx.append("\n" + sFrom + ": " + sMessage);
        }
    }

    /**
     * Process events that are triggered by messages that are received by the server
     * When a new message arrives it triggers an event that is captured here and the contents of the event are processed.
     * This mostly deals with message displaying; It is a passive reaction to an event. This can, however, cause other parts of the protocol or the UI to
     * change state. 
     * @param evt - the MessageReceivedEvent object
     */
    private void processEvent(MessageReceivedEvent evt) {
	JTextArea textArea = testArea; // Default
	switch(evt.getEventType()) { // we are driven by the event type
            case DPCCore.messages.MessageTypes.Error: 
                javax.swing.JOptionPane.showConfirmDialog(this,evt.getError().Message);
            break;
	    case DPCCore.messages.MessageTypes.UPDATE_MASTER_CHAT_LIST: break;
	    case DPCCore.messages.MessageTypes.SEND_MASTER_CHAT_LIST: 
                chatRooms.clear();
        try{
                for (Iterator<DPCChatGroup> it = evt.getMasterChatList().getChatGroups().iterator(); it.hasNext();) {
            DPCChatGroup g = it.next();
	     chatGroups.put(g.Title, g);
            chatRooms.addElement(g.Title);	    
        }
        }catch(Exception e){javax.swing.JOptionPane.showConfirmDialog(this,"Error Parsing Mster Chat List");}
                //chat
                break;
	    case DPCCore.messages.MessageTypes.STATUS_OF_CHAT_GROUP: 
                if(!Peer_TextAreas.containsKey(evt.getStatusOfChatGroup().Title)) {
                    createNewChatroomWindow(evt.getStatusOfChatGroup().Title, this.globalDPCInstance);
                    textArea.append("\n\n"+evt.getStatusOfChatGroup().Title+"\n\n"+evt.getStatusOfChatGroup().Description+"\n\n"); 
                    chatGroups.put(evt.getStatusOfChatGroup().Title, evt.getStatusOfChatGroup());
                }
		    updateBuddyList(evt.getStatusOfChatGroup().Title, evt.getStatusOfChatGroup());
                    textArea = (JTextArea)Peer_TextAreas.get(DPCInstance.getGroup(evt.getStatusOfChatGroup().ThreadID).Title);
                    break;
	    
            case DPCCore.messages.MessageTypes.SEND_MESSAGE:
		
//	if(Peer_TextAreas.containsKey(evt.getMessage().Origin.Nick)) { 
//		    textArea = (JTextArea)Peer_TextAreas.get(evt.getMessage().Origin.Nick);
//		    textArea.append("\n"+evt.getMessage().Origin.Nick+": "+evt.getSendMessage().Message);
//		}
		SealedObject sObj = null;		
		if(evt.getMessage().getSealedObject() != null ) {
		    System.out.println("RECEIVED Encrypted message from "+evt.getMessage().Origin.Nick);
		    sObj = evt.getMessage().getSealedObject();
		    //System.out.println(DPCInstance.encryptionHandler.decrypt(sObj));
		}
		System.out.println("NICK="+evt.getMessage().Origin.Nick + " and Group="+evt.getMessage().Destination.ThreadID);		
                if((evt.getMessage().Destination.ThreadID!=null && !evt.getMessage().Destination.ThreadID.equals("")) 
			&& Peer_TextAreas.containsKey(DPCInstance.getGroup(evt.getMessage().Destination.ThreadID).Title)) { 
		    System.out.println("NICK="+evt.getMessage().Destination.Nick);
		    textArea = (JTextArea)Peer_TextAreas.get(DPCInstance.getGroup(evt.getMessage().Destination.ThreadID).Title);
		    if(sObj != null) {
			textArea.append("\n"+evt.getMessage().Origin.Nick+"**: "+DPCInstance.encryptionHandler.decrypt(sObj));
		    }
		    else {
			textArea.append("\n"+evt.getMessage().Origin.Nick+": "+evt.getSendMessage().Message);
		    }
		}
                
                else if (DPCInstance.isGroup(evt.getMessage().Destination.ThreadID))
               { 
                    if(!Peer_TextAreas.containsKey(evt.getMessage().Destination.ThreadID)) {
                         createNewChatroomWindow(DPCInstance.getGroup(evt.getMessage().Destination.ThreadID).Title, this.globalDPCInstance);
                    }
                    updateBuddyList(DPCInstance.getGroup(evt.getMessage().Destination.ThreadID).Title, DPCInstance.getGroup(evt.getMessage().Destination.ThreadID));
                    textArea = (JTextArea)Peer_TextAreas.get(DPCInstance.getGroup(evt.getMessage().Destination.ThreadID).Title);
                    textArea.append("\n\n"+DPCInstance.getGroup(evt.getMessage().Destination.ThreadID).Title+"\n\n"+DPCInstance.getGroup(evt.getMessage().Destination.ThreadID).Description+"\n\n");
                }
                else if(evt.getMessage().Origin.Nick.equals(Nick_CURRENT)) {
		    System.out.println(">>>>>>>> I FOUND MYSELF <<<<<<<<<<<<"); break; // we don't want to display messages from ourselves to ourselves
		}
                else {
		    // here are the private messages
		    if(!Peer_TextAreas.containsKey(evt.getMessage().Origin.Nick)){
			
		    //if(evt.getMessage().Destination.ThreadID == null && !evt.getMessage().Destination.ThreadID.equals("")) {			
			// We have a peer message for a peer that we don't have a TAB for. So we need to create one
                        String name = "";
			name = evt.getMessage().Origin.Nick;
                                               
			createPrivateChat(evt.getMessage().Origin.Nick, globalDPCInstance);
			textArea = (JTextArea)Peer_TextAreas.get(evt.getMessage().Origin.Nick);
			if(sObj != null) {
			    textArea.append("\n"+evt.getMessage().Origin.Nick+"**: "+DPCInstance.encryptionHandler.decrypt(sObj));
			}
			else {
			    textArea.append("\n"+evt.getMessage().Origin.Nick+": "+evt.getSendMessage().Message);
			}
		    }
		    else {
			//System.out.println("I got here 3");
			textArea = (JTextArea)Peer_TextAreas.get(evt.getMessage().Origin.Nick);
			if(sObj != null) {
			    textArea.append("\n"+evt.getMessage().Origin.Nick+"**: "+DPCInstance.encryptionHandler.decrypt(sObj));
			}
			else {
			    textArea.append("\n"+evt.getMessage().Origin.Nick+": "+evt.getSendMessage().Message);
			}
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
	    case DPCCore.messages.MessageTypes.CONNECT_TO_CHAT_GROUP: 
                break;
	    case DPCCore.messages.MessageTypes.DPC_MESSAGE: break;
	    
	    default: // We always want to have a default error handling state
		textArea.append("\nEvent triggered "+evt.getMessage().Origin.Nick+" with type: "+evt.getEventType());
		System.out.println("Event triggered "+evt.getMessage().Origin.Nick+" with type: "+evt.getEventType()); break;
	}
    }

    /**
     * The main method of this class - this starts the MainScreen GUI applicaiton for the DPC Chat
     * @param args 
     */
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
			try{
                        displayMainScreen(new DPCCore.DPCInstance(1916, "James Connally"));
			} catch(Exception e3) {
			    displayMainScreen(new DPCCore.DPCInstance(1917, "Billy the kid"));
			}
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
            }
        });
    }
}
