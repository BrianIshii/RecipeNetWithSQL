package Entity;

public class Instruction implements Entity {
  private Long instrid;
  private Long rid;
  private Integer step;
  private String description;

  public Instruction(Long instrid, Long rid, Integer step, String description) {
    this.instrid = instrid;
    this.rid = rid;
    this.step = step;
    this.description = description;
  }

  public Instruction(Long rid, Integer step, String description) {
    this.instrid = 0L;
    this.rid = rid;
    this.step = step;
    this.description = description;
  }

  public Long getInstrid() {
    return instrid;
  }

  public Long getRid() {
    return rid;
  }

  public Integer getStep() {
    return step;
  }

  public String getDescription() {
    return description;
  }

  public void setInstrid(Long instrid) {
    this.instrid = instrid;
  }

  public void setRid(Long rid) {
    this.rid = rid;
  }
}
