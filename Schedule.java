//Schedule manages and schedules Process objects attempting to use the CPU using queues
//Inherited by ClockPolicy and LruPolicy
//Created by: Adam Crocker, c3192396 for COMP2240
import java.util.ArrayDeque;
import java.util.PriorityQueue;

public abstract class Schedule {

  private PriorityQueue<Process> readyQueue;  //processes that are ready to execute wait here
  private ArrayDeque<Process> blockQueue; //process w/o page in memory wait here
  private PriorityQueue<Process> outputQueue; //hold finished process for data output

  private Process CPU;  //Hold currently executing process
  private int quantum;  //Max time units a process can stay in CPU in one go
  private int time = 0; //Simulation time
  private int frameAlloc; //Amount of frames available to each process


  public Schedule(int totalFrames, int quantum, PriorityQueue<Process> processes) {
    readyQueue = processes;
    blockQueue = new ArrayDeque<>();
    outputQueue = new PriorityQueue<>();
    this.quantum = quantum;
    frameAlloc = totalFrames / readyQueue.size(); //frames are divided evenly among processes
    initProcessFrames();
  }

  public abstract void checkAllIO();

  public abstract String getPolicyHeader();

  //Assign allocated frames to each process
  public void initProcessFrames() {
    for(Process p : readyQueue)
      p.initFrames(frameAlloc);
  }

  public int getTime() {
    return time;
  }

  public int getQuantum() {
    return quantum;
  }

  //Time increments only if a process executes a line or all processes are blocked
  public void incrementTime() {
    time++;
  }

  //Send process to CPU to execute page lines
  public void sendToCPU(Process p) {
    CPU = p;
  }

  //Checks if CPU has a process in it
  public boolean CPUisEmpty() {
    return CPU == null;
  }

  //Removes Process from CPU
  public Process sendFromCPU() {
    Process temp = CPU;
    CPU = null;
    return temp;
  }

  //Returns Process currently in CPU
  public Process getCPUProcess() {
    return CPU;
  }

  public PriorityQueue<Process> getReadyQueue() {
    return readyQueue;
  }

  public ArrayDeque<Process> getBlockQueue() {
    return blockQueue;
  }

  //Only blocked processes have IO requests, decrements wait time after time advances
  public void decrementAllIO() {
    if(!getBlockQueue().isEmpty()) {
      for(Process p : getBlockQueue()) {
        p.decrementIO();
      }
    }
  }

  //Checks if all active processes are in blocked queue so time can keep advancing
  public Boolean allBlocked() {
    return getReadyQueue().isEmpty() && CPUisEmpty() && !getBlockQueue().isEmpty();
  }

  //unblocks valid processes before anything happens in run loop
  public void checkIfUnblocked() {
    for(Process p : blockQueue) {
      if(p.checkPage() != -1) {
        p.setTimeReady(time);
        readyQueue.offer(p);
        blockQueue.remove(p);
      }
    }
  }

  //Outputs before process data output
  public void printOutputHeader() {
    System.out.println("PID  Process Name      Turnaround Time  # Faults  Fault Times");
  }

  public PriorityQueue<Process> getOutputQueue() {
    return outputQueue;
  }

  //Simulation logic, runs until all processes have executed their page data
  public void run() {
    //loop until all processes are finished
    while(!getReadyQueue().isEmpty() || !CPUisEmpty() || !getBlockQueue().isEmpty()) {
      checkAllIO(); //Check if any IO requests can be sent to memory
      checkIfUnblocked(); //check if any blocked processes can unblock
      //CPU is Empty
      if(CPUisEmpty() && !getReadyQueue().isEmpty()) {
        //send process to CPU
        sendToCPU(getReadyQueue().poll());
        getCPUProcess().setQuantumLeft(getQuantum());
      }
      //increment time if all processes are blocked
      else if(allBlocked()) {
        incrementTime();
        decrementAllIO();
      }
      //CPU has process
      else if(!CPUisEmpty()) {
        //check is CPU process page is in memory
        if (getCPUProcess().checkPage() == -1) {
          getCPUProcess().pageFault(getTime());
          getBlockQueue().add(sendFromCPU());
        }
        //process has finish executing
        else if (getCPUProcess().getPageRequests().isEmpty()) {
          getCPUProcess().setFinishTime(getTime());
          getCPUProcess().setTimeReady(-1);
          getOutputQueue().offer(getCPUProcess());
          sendToCPU(null);
        }
        //quantum has expired and other processes are waiting in queue
        else if (getCPUProcess().getQuantumLeft() == 0 && !getReadyQueue().isEmpty()) {
          getCPUProcess().setTimeReady(getTime());
          getReadyQueue().offer(sendFromCPU());
        }
        //time increments
        else {
          //Process is in CPU but the next page to execute is not in memory
          if(getCPUProcess().checkPage() == -1) {
            getCPUProcess().pageFault(getTime());
            getBlockQueue().add(sendFromCPU());
          }
          else {
            //Process executes page, time increases
            getCPUProcess().setQuantumLeft(getCPUProcess().getQuantumLeft() - 1);
            getCPUProcess().executeLine(getTime());
            incrementTime();
            decrementAllIO();
          }
        }
      }
    }
    //Data output
    System.out.println(getPolicyHeader());
    printOutputHeader();
    while(!getOutputQueue().isEmpty())
      getOutputQueue().poll().printResults();
  }
}
