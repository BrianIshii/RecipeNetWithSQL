package Entity;

public class Ingredient extends Entity {
  public static final String TABLE_NAME = "Ingredient";

  public Ingredient(Long iid, String name) {
    this.setStatus(Status.DIRTY);
    initializeFields(iid, name);
  }

  public Ingredient(String name) {
    this.setStatus(Status.NEW);
    initializeFields(null, name);
  }

  public Ingredient(Long iid) {
    this.setStatus(Status.NEW);
    initializeFields(iid, null);
  }

  public Ingredient() {
    this.setStatus(Status.NEW);
    initializeFields(null, null);
  }

  private void initializeFields(Long iid, String name) {
    addField(Long.class, "iid", iid == null ? 0L : iid, true);
    addField(String.class, "name", name, false);
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
