package DPCCore;

/**
 * DPCConstants.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * Place to keep all the constant as an effort to limit random magical numbers (ports for example)
 */
public final class DPCConstants {
    public static int MASTER_SERVER_PORT = 1977; // The master server port
    public static int PingTimeout = 10000; // The timeout in milliseconds
    public static int UpdateChatGroupTimeout = 15000; // Timeout in milliseconds
enum State {NOT_CONNECTED, PROVISIONED_TO_CONNECTION, PEER_STATE, ELECTION_STATE, ADMIN} // Enumeration for the states that a peer can be in
// Enmueration for the error states
public enum ErrorState {NONE, CMD_NOT_SUPPORTED,NOT_ALLOWED,MISSING_SECURITY_CREDENTIALS,REJECT_CONNECTION,NO_THREAD,REJECT_KEY_CHANGE_REQUEST,INVALID_MASTER_SERVER}
}
