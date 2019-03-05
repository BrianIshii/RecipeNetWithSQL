package Entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Recipe extends Entity {
  private static String TABLE_NAME = "Recipe";
  private User user;
  private List<Ingredient_Recipe> ingredients;
  private List<Instruction> instructions;

  public Recipe(Long rid, String title, String url, User user, Date date, Integer rating) {
    this.setStatus(Status.DIRTY);
    this.user = user;
    this.ingredients = new ArrayList<Ingredient_Recipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(rid, title, url, user, date, rating);
  }

  public Recipe(String title, String url, User user, Date date, Integer rating) {
    this.setStatus(Status.NEW);
    this.user = user;
    this.ingredients = new ArrayList<Ingredient_Recipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(null, title, url, user, date, rating);
  }

  private void initializeFields(
      Long rid, String title, String url, User user, Date date, Integer rating) {
    addField(Long.class, "rid", rid == null ? 0L : rid, true);
    addField(String.class, "title", title, false);
    addField(String.class, "url", url, false);
    addField(Long.class, "uid", (Long) user.getPrimaryKey().get(0).getValue(), false);
    addField(Date.class, "date", date, false);
    addField(Integer.class, "rating", (Integer) rating, false);
  }

  public void addIngredient(Ingredient ingredient, Integer amount, String unit) {
    Ingredient_Recipe ingredient_recipe = new Ingredient_Recipe((Long) this.getValue("rid"), ingredient, amount, unit);
    this.ingredients.add(ingredient_recipe);
  }

  public void removeIngredient(Ingredient ingredient) {
    for (Ingredient_Recipe i : this.ingredients) {
      if (i.equals(ingredient)) {
        i.setStatus(Status.DELETED_LOCALLY);
      }
    }
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
