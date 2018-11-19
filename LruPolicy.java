//LruPolicy implements the Last Recently Used frame management rules into Schedule
//Calls LRU related methods in process
//Frames are overwritten based on longest time since accessed
import java.util.PriorityQueue;

public class LruPolicy extends Schedule{

  public LruPolicy(int totalFrames, int quantum, PriorityQueue<Process> processes) {
    super(totalFrames, quantum, processes);
  }

  //Overwrites Frames in Process with valid IORequests using LRU policy
  @Override
  public void checkAllIO() {
    if(!getBlockQueue().isEmpty()) {
      for(Process p : getBlockQueue()) {
        if(!p.getIoQueue().isEmpty() && p.getIoQueue().peek().getTimeLeft() == 0) {
          p.findLastUsedFrame();
          p.sendPageToMemory();
        }
      }
    }
  }

  //Data Output label
  @Override
  public String getPolicyHeader() {
    return "LRU - Fixed:";
  }

}
