package Entity;

import java.sql.Date;
import java.util.List;

public class Recipe extends Entity {
  private static String TABLE_NAME = "Recipe";
  private User user;
  private List<Ingredient_Recipe> ingredients;
  private List<Instruction> instructions;

  public Recipe(Long rid, String title, String url, User user, Date date, Integer rating) {
    this.setStatus(Status.DIRTY);
    this.user = user;
    addField(Long.class, "rid", rid, true);
    addField(String.class, "title", title, false);
    addField(String.class, "url", url, false);
    addField(Long.class, "uid", (Long) user.getPrimaryKey().get(0).getValue(), false);
    addField(Date.class, "date", date, false);
    addField(Integer.class, "rating", (Integer) rating, false);
  }

  public Recipe(String title, String url, User user, Date date, Integer rating) {
    this.setStatus(Status.NEW);
    this.user = user;
    addField(Long.class, "rid", 0L, true);
    addField(String.class, "title", title, false);
    addField(String.class, "url", url, false);
    addField(Long.class, "uid", (Long) user.getPrimaryKey().get(0).getValue(), false);
    addField(Date.class, "date", date, false);
    addField(Integer.class, "rating", (Integer) rating, false);
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
