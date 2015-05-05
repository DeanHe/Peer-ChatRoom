/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DPCCore.messages;

/**
 *
 * @author AMulroney
 */
public class SendMessage extends DPCGenericObject {
    public String Message;
    
    public SendMessage(String Message)
    {
        this.Message=Message;
    }
}
