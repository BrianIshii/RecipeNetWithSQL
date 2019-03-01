package Entity;

public class Ingredient extends Entity {
  private static String TABLE_NAME = "Recipe";

  public Ingredient(Long iid, String name) {
    this.setStatus(Status.DIRTY);
    addField(Long.class, "iid", iid, true);
    addField(String.class, "name", name, false);
  }

  public Ingredient(String name) {
    this.setStatus(Status.NEW);
    addField(Long.class, "iid", 0L, true);
    addField(String.class, "name", name, false);
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
