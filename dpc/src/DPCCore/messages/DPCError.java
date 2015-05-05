
package DPCCore.messages;
import DPCCore.*;
import DPCCore.DPCConstants.ErrorState;
/**
 * DPCError.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * Report Error convey information back to a client.
 * Display the Client feature.
 */
public class DPCError extends DPCGenericObject {
    public DPCConstants.ErrorState ID= DPCConstants.ErrorState.NONE;
    public String Message="";
    
    public DPCError(DPCConstants.ErrorState errState)
    {
        switch (errState)
        {
            case  CMD_NOT_SUPPORTED:
            case NOT_ALLOWED:
            case MISSING_SECURITY_CREDENTIALS:
            case REJECT_CONNECTION:
                Message="Could not connect to chat group.";
                break;
            case NO_THREAD:
            case REJECT_KEY_CHANGE_REQUEST:
                Message="Mistake: Jerry Rawling never would have done this.";
                break;
            case INVALID_MASTER_SERVER:
                Message="It appears the Master Chat Server Specified does not exist.";
                
                
        }
    }
}
