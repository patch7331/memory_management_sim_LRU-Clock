import java.util.ArrayDeque;

public class Process {

  private int ID;
  private ArrayDeque<Integer> pageRequests;

  public Process(int ID) {
    this.ID = ID;
    pageRequests = new ArrayDeque<>();
  }

  public void addRequest(int page) {
    pageRequests.push(page);
  }

  public void checkPages() {
    System.out.println("ID: " + ID);
    for(Integer i : pageRequests)
      System.out.println(i);
  }

}
