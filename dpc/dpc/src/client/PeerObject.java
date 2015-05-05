package client;

/**
 *
 * @author dd599
 */
public class PeerObject {
    private String PeerName = "";
    private String IP = "";
    private String Port = "";
    
    public PeerObject(String sPN, String sIP, String sPrt) {
	this.IP=sIP;
	this.Port = sPrt;
	this.setPeerName(sPN);
    }

    public String getPeerName() {
	return PeerName;
    }

    public String getIP() {
	return IP;
    }

    public String getPort() {
	return Port;
    }

    public void setPeerName(String PeerName) {
	this.PeerName = PeerName;
    }

    public void setIP(String IP) {
	this.IP = IP;
    }

    public void setPort(String Port) {
	this.Port = Port;
    }
    
}
