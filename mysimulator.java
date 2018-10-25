import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class mysimulator {

  public static void main(String[] args) throws IOException {

    if(args.length < 3) {
      System.out.println("Argument error");
      System.exit(0);
    }
    int frames = Integer.parseInt(args[0]);
    int quantum = Integer.parseInt(args[1]);
    ArrayList<Process> processList = new ArrayList<>();

    int id = 0;
    for(int i = 2; i < args.length; i++) {
      FileReader fr = new FileReader(args[i]);
      BufferedReader reader = new BufferedReader(fr);
      String line;
      if(!(line = reader.readLine()).equals("begin")) {
        System.out.println("Error in file read, 'begin' not found");
        System.exit(0);
      }
      Process p = new Process(id);
      while(!(line = reader.readLine()).equals("end")) {
        p.addRequest(Integer.parseInt(line));
      }
      processList.add(p);
      id++;
    }

    System.out.println("Frames: " + frames);
    System.out.println("Quantum: " + quantum);
    for(Process p : processList)
      p.checkPages();

  }
}
