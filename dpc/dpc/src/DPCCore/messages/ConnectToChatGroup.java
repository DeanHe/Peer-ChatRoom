/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DPCCore.messages;


/**
 *
 * @author AMulroney
 */
    public class ConnectToChatGroup extends DPCGenericObject {
        public String ThreadID;
        public String PublicKey;
        
        public ConnectToChatGroup(String ThreadID, String PK)
        {
            this.ThreadID = ThreadID;
            this.PublicKey = PK;
        }
    }
