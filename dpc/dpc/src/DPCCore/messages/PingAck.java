/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DPCCore.messages;


/**
 *
 * @author AMulroney
 */
public class PingAck extends DPCGenericObject {
    public int id;
    
    public PingAck(Ping ping)
    {
        this.id = ping.id+1;
    }
}
