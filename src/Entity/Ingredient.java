package Entity;

public class Ingredient implements Entity {
  private Long iid;
  private String name;

  public Ingredient(Long iid, String name) {
    this.iid = iid;
    this.name = name;
  }

  public Ingredient(String name) {
    this.iid = 0L;
    this.name = name;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Ingredient{");
    sb.append("iid=").append(iid);
    sb.append(", name='").append(name).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public Long getIid() {
    return iid;
  }

  public String getName() {
    return name;
  }

  public void setIid(Long iid) {
    this.iid = iid;
  }
}
