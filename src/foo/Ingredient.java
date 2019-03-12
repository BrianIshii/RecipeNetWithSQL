package foo;

import schema.Schema;

public class Ingredient extends Entity {
  public static final String TABLE_NAME = "Ingredient";
  public static final Schema ENTITY_FIELDS =
      new Schema()
          .addField(Long.class, "iid", null, true)
          .addField(String.class, "name", null, false);

  public Ingredient(Long iid, String name) {
    initializeFields(iid, name);
    this.setDirty();
  }

  public Ingredient(String name) {
    initializeFields(null, name);
  }

  public Ingredient(Long iid) {
    initializeFields(iid, null);
  }

  public Ingredient() {
    initializeFields(null, null);
  }

  private void initializeFields(Long iid, String name) {
    fields = deepCopyFields(ENTITY_FIELDS);
    this.setFieldValue("iid", iid == null ? 0L : iid);
    this.setFieldValue("name", name);
    setNew();
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
