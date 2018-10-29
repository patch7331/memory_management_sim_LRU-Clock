//Process is created from files containing a list of memory pages to be accessed
//It will keep attempted to access the CPU until pageRequests is empty
//Each Process has its own list of Frames to access, which is managed based on sim policy
//When a process tries to execute a page in memory its blocked and issues an IO request
//Created by: Adam Crocker, c3192396 for COMP2240
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Process implements Comparable<Process>{

  private int ID;
  private String name;
  private int finishTime;
  private int quantumLeft;
  private int frameIndex;
  private int timeReady = 0;
  private ArrayDeque<Integer> pageRequests;
  private ArrayList<Integer> faultTimes;
  private ArrayList<Frame> frameList;
  private ArrayDeque<IORequest> ioQueue;

  public Process(int ID, String name) {
    this.ID = ID;
    this.name = name;
    pageRequests = new ArrayDeque<>();
    faultTimes = new ArrayList<>();
    frameList = new ArrayList<>();
    frameIndex = frameList.size()-1;
    ioQueue = new ArrayDeque<>();
  }

  //Clone constructor, I couldnt get it to work
  public Process(Process p) {
    this.ID = p.getID();
    this.name = p.getName();
    this.pageRequests = p.getPageRequests();
    this.faultTimes = p.getFaultTimes();
    this.frameList = p.getFrameList();
    this.frameIndex = p.getFrameIndex();
    this.ioQueue = p.getIoQueue();
    this.finishTime = p.getFinishTime();
    this.quantumLeft = p.getQuantumLeft();
    this.timeReady = p.getTimeReady();
  }

  //Once all processes are created the total frames can be divided and sent to each
  public void initFrames(int frames) {
    for(int i = 0; i < frames; i++) {
      Frame frame = new Frame();
      frameList.add(frame);
    }
  }

  //checks if next page to be executed is already in the memory
  public int checkPage() {
    for(int i = 0;i < frameList.size();i++) {
      if(frameList.get(i).getPage() == checkNextPage())
        return i;
    }
    return -1;
  }

  //Updates frame and deletes executed page line from pageRequests
  public void executeLine(int time) {
    frameList.get(frameIndex).setUse(0);
    frameList.get(frameIndex).setLastUsed(time);
    pageRequests.remove();
  }

  //Finds frame to overwrite using LRU Policy
  public void findLastUsedFrame() {
    int lastUsed = Integer.MAX_VALUE;
    frameIndex = 0;
    for(int i = 0; i < frameList.size(); i++) {
      if(frameList.get(i).getLastUsed() < lastUsed) {
        lastUsed = frameList.get(i).getLastUsed();
        frameIndex = i;
      }
    }
  }

  //Finds frame to overwrite using Clock Policy
  public void nextIndexClock() {
    if(frameIndex != frameList.size()-1)
      frameIndex++;
    else
      frameIndex = 0;
  }

  //Precondition: Call findLastUsedFrame() or nextIndexClock() first to assign correct frame
  //Assigns IO page request to memory
  public void sendPageToMemory() {
    frameList.get(frameIndex).setPage(ioQueue.remove().getPage());
    frameList.get(frameIndex).setUse(1);
  }

  //Creates a new IO request to put unavailable page into memory
  public void createIORequest(int page) {
    IORequest request = new IORequest(page);
    ioQueue.add(request);
  }

  //Reduces IORequest wait time after each time loop
  public void decrementIO() {
    for(IORequest i : ioQueue) {
      i.decreaseTime();
    }
  }

  public ArrayDeque<IORequest> getIoQueue() {
    return ioQueue;
  }

  //Page was not found in memory, log time and create IO request
  public void pageFault(int time) {
    getFaultTimes().add(time);
    createIORequest(checkNextPage());
  }


  public void addRequest(int page) {
    pageRequests.add(page);
  }

  //Returns next page to be executed in CPU
  public int checkNextPage() {
    if(pageRequests.isEmpty())
      return -1;
    return pageRequests.peek();
  }

  public ArrayDeque<Integer> getPageRequests() {
    return pageRequests;
  }

  public int getID() {
    return ID;
  }

  public int getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(int finishTime) {
    this.finishTime = finishTime;
  }

  public int getQuantumLeft() {
    return quantumLeft;
  }

  public void setQuantumLeft(int quantumLeft) {
    this.quantumLeft = quantumLeft;
  }

  public ArrayList<Integer> getFaultTimes() {
    return faultTimes;
  }

  //Data output
  public void printResults() {
    String out = "";
    out += String.format("%-5s",ID);
    out += String.format("%-18s",name);
    out += String.format("%-17s",finishTime);
    out += String.format("%-10s",faultTimes.size());

    out += "{";

    for(Integer i : faultTimes) {
      out += i + ", ";
    }
    out = out.substring(0,out.length()-2);
    out += "}";
    System.out.println(out);
  }

  public int getTimeReady() {
    return timeReady;
  }

  public void setTimeReady(int timeReady) {
    this.timeReady = timeReady;
  }

  public String getName() {
    return name;
  }

  public int getFrameIndex() {
    return frameIndex;
  }

  public ArrayList<Frame> getFrameList() {
    return frameList;
  }

  //Used by PriorityQueue, if two processes are ready at same time the smaller ID goes first
  @Override
  public int compareTo(Process process) {
    if(timeReady == process.getTimeReady()) {
      if(ID < process.getID())
        return -1;
      return 1;
    }
    else if(timeReady < process.getTimeReady())
      return -1;
    return 1;
  }
}
