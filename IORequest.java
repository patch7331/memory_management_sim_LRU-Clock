//IORequests are created when a process attempts to access a page that is not in memory
//IO requests last for 6 time units then they overwrite a frame based on a policy
//Created by: Adam Crocker, c3192396 for COMP2240
public class IORequest {

  private int page;
  private int timeLeft;

  public IORequest(int page) {
    this.page = page;
    timeLeft = 6;
  }

  public int getPage() {
    return page;
  }

  public int getTimeLeft() {
    return timeLeft;
  }

  public void decreaseTime() {
    timeLeft--;
  }
}
