package RecipeNet.entity;

import RecipeNet.schema.Schema;

public class Ingredient extends Entity {
  public static final String TABLE_NAME = "Ingredient";
  public static final String IID = "iid";
  public static final String NAME = "name";
  public static final Schema ENTITY_FIELDS =
      new Schema().addField(Long.class, IID, true).addField(String.class, NAME, false);

  public Ingredient(Long iid, String name) {
    initializeFields(iid, name);
    this.setDirty();
  }

  public Ingredient(String name) {
    initializeFields(null, name);
  }

  public Ingredient(Long iid) {
    initializeFields(iid, null);
    setDirty();
  }

  public Ingredient() {
    initializeFields(null, null);
  }

  private void initializeFields(Long iid, String name) {
    fields = deepCopyFields(ENTITY_FIELDS);
    this.setFieldValue(IID, iid == null ? 0L : iid);
    this.setFieldValue(NAME, name);
    setNew();
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
