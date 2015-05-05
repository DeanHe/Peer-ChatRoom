package DPCCore;
import DPCCore.messages.*;


/**
 * DPCStateMonitor.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * DPCStateMonitor keeps track of the current states a peer is in.
 * It fulfills the Statefull requirement
*/


public class DPCStateMonitor extends Thread {
    DPCGenericObject origin;
    String command;
    
    public DPCStateMonitor(DPCGenericObject o, String d)
    {
        origin = o;
        command = d;
    }
  @Override
    public void run() {
      if (origin==null) return;
        synchronized(origin){
        try{
            int wait = 0;
            switch (command)
            {
                case "Ping":
                case "PingAck":
                    wait = 6000;
                break;
                default:
                    wait = 10000;
            }
            
            origin.wait(wait);
            //DPCInstance.Nick = "fail";
            DPCInstance.StateCallback(this);
        }catch (InterruptedException e)
        {
            DPCInstance.StateCallback(this);
        }
        }
    }
    
}
