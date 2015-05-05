/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DPCCore;

/**
 *
 * @author AMulroney
 */
public class DPCStateMonitor implements Runnable {
    Origin origin;
    String dispatched;
    
    public DPCStateMonitor(Origin o, String d)
    {
        origin = o;
        dispatched = d;
    }
    public void run()
    {
        try{
            origin.wait(10000);
            DPCInstance.Nick = "fail";
        }catch (InterruptedException e)
        {
            DPCInstance.Nick = "hurray";
        }
        
    }
}
