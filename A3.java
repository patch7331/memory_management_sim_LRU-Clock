//A3 runs a simple simulation of CPU processing with limited memory using
//Round Robin scheduling
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;

public class A3 {

  public static void main(String[] args) throws IOException {

    if(args.length < 3) {
      System.out.println("Argument error");
      System.exit(0);
    }
    int frames = Integer.parseInt(args[0]); //available memory to be divided among processes
    int quantum = Integer.parseInt(args[1]);  //time processes will spend in CPU

    //Run sim using Last Recently Used Policy
    LruPolicy lru = new LruPolicy(frames,quantum, createProcessList(args));
    lru.run();
    //Run sim using Clock Policy
    ClockPolicy cp = new ClockPolicy(frames, quantum, createProcessList(args));
    cp.run();
  }

  //Parses file data into Process objects and stores them in a list
  public static PriorityQueue<Process> createProcessList(String args[])
      throws IOException {
    PriorityQueue<Process> processList = new PriorityQueue<>();

    int id = 1;
    for(int i = 2; i < args.length; i++) {
      FileReader fr = new FileReader(args[i]);
      BufferedReader reader = new BufferedReader(fr);
      String line;
      if(!(line = reader.readLine()).equals("begin")) {
        System.out.println("Error in file read, 'begin' not found");
        System.exit(0);
      }
      Process p = new Process(id,args[i]);
      while(!(line = reader.readLine()).equals("end")) {
        p.addRequest(Integer.parseInt(line));
      }

      processList.offer(p);
      id++;
    }
    return processList;
  }

  //My failed attempt to deep clone the processList instead of parsing the files twice
  //Feedback/link on why I failed would be appreciated
  public static PriorityQueue<Process> deepClone(PriorityQueue<Process> pl) {
    PriorityQueue<Process> cloneList = new PriorityQueue<>();
    for(Process p : pl) {
      cloneList.offer(deepCloneProcess(p));
    }
    return cloneList;
  }

  public static Process deepCloneProcess(Process p) {
    Process newP = new Process(p);
    return newP;
  }

}

