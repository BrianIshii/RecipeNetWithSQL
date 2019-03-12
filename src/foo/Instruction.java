package foo;

import schema.Schema;

public class Instruction extends Entity {
  public static final String TABLE_NAME = "Instruction";
  public static final Schema ENTITY_FIELDS =
      new Schema()
          .addField(Long.class, "instrid", 0L, true)
          .addField(Long.class, "rid", null, false)
          .addField(Integer.class, "step", null, false)
          .addField(String.class, "description", null, false);

  public Instruction(Long instrid, Long rid, Integer step, String description) {
    initializeFields(instrid, rid, step, description);
    setDirty();
  }

  public Instruction(Long rid, Integer step, String description) {
    initializeFields(null, rid, step, description);
  }

  public Instruction(Long rid) {
    initializeFields(null, rid, null, null);
  }

  private void initializeFields(Long instrid, Long rid, Integer step, String description) {
    fields = deepCopyFields(ENTITY_FIELDS);
    setFieldValue("instrid", instrid == null ? 0L : instrid);
    setFieldValue("rid", rid);
    setFieldValue("step", step);
    setFieldValue("description", description);
    setNew();
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
