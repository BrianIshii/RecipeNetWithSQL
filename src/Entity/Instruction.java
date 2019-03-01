package Entity;

public class Instruction extends Entity {
  public static final String TABLE_NAME = "Instruction";

  public Instruction(Long instrid, Long rid, Integer step, String description) {
    this.setStatus(Status.DIRTY);
    initializeFields(instrid, rid, step, description);
  }

  public Instruction(Long rid, Integer step, String description) {
    this.setStatus(Status.NEW);
    initializeFields(null, rid, step, description);
  }

  private void initializeFields(Long instrid, Long rid, Integer step, String description) {
    addField(new Field<>(Long.class, "instrid", instrid == null ? 0L : instrid, true));
    addField(new Field<>(Long.class, "rid", rid, false));
    addField(new Field<>(Integer.class, "step", step, false));
    addField(new Field<>(String.class, "description", description, false));
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
