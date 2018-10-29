//Frame holds a page that processes need to execute, each process is assigned a certain amount of
//frames
//Created by: Adam Crocker, c3192396 for COMP2240
public class Frame {

  private int page = -1;
  private int use = 0;
  private int lastUsed = 0;

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  //not used in current implementation
  public int getUse() {
    return use;
  }

  public void setUse(int use) {
    this.use = use;
  }

  public int getLastUsed() {
    return lastUsed;
  }

  public void setLastUsed(int lastUsed) {
    this.lastUsed = lastUsed;
  }
}
