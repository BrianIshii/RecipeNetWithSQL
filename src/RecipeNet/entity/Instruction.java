package RecipeNet.entity;

import RecipeNet.schema.Schema;

public class Instruction extends Entity {
  public static final String TABLE_NAME = "Instruction";
  public static final String INSTRID = "instrid";
  public static final String RID = "rid";
  public static final String STEP = "step";
  public static final String DESCRIPTION = "description";
  public static final Schema ENTITY_FIELDS =
      new Schema()
          .addField(Long.class, INSTRID, 0L, true)
          .addField(Long.class, RID, false)
          .addField(Integer.class, STEP, false)
          .addField(String.class, DESCRIPTION, false);

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
    setFieldValue(INSTRID, instrid == null ? 0L : instrid);
    setFieldValue(RID, rid);
    setFieldValue(STEP, step);
    setFieldValue(DESCRIPTION, description);
    setNew();
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
