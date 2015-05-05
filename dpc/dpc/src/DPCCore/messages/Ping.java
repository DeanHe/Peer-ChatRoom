/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DPCCore.messages;

/**
 *
 * @author AMulroney
 */
public class Ping extends DPCGenericObject {
    public int id;
    
    public Ping()
    {
        
        this.id=(int )(Math.random() * 5000 + 1);
    }
}
