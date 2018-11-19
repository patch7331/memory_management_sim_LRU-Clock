//ClockPolicy implements the Clock Policy frame management rules into Schedule
//Calls clock policy related methods in process
//Frames are overwritten in a circular fashion, like a type of clock array
import java.util.PriorityQueue;

public class ClockPolicy extends Schedule {

  public ClockPolicy(int totalFrames, int quantum, PriorityQueue<Process> processes) {
    super(totalFrames, quantum, processes);
  }

  //Overwrites Frames in Process with valid IORequests using clock policy
  @Override
  public void checkAllIO() {
    if(!getBlockQueue().isEmpty()) {
      for(Process p : getBlockQueue()) {
        if(!p.getIoQueue().isEmpty() && p.getIoQueue().peek().getTimeLeft() == 0) {
          p.nextIndexClock();
          p.sendPageToMemory();
        }
      }
    }
  }

  //Data Output label
  @Override
  public String getPolicyHeader() {
    return "Clock - Fixed:";
  }


}
