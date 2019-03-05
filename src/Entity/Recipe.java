package Entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Recipe extends Entity {
  public static final String TABLE_NAME = "Recipe";
  private User user;
  private List<IngredientRecipe> ingredients;
  private List<Instruction> instructions;

  public Recipe(Long rid) {
    this.setStatus(Status.DIRTY);
    this.ingredients = new ArrayList<IngredientRecipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(rid, null, null, null, null, null);
  }

  public Recipe(Long rid, String title, String url, User user, Date date, Integer rating) {
    this.setStatus(Status.DIRTY);
    this.user = user;
    this.ingredients = new ArrayList<IngredientRecipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(rid, title, url, user, date, rating);
  }

  public Recipe(String title, String url, User user, Date date, Integer rating) {
    this.setStatus(Status.NEW);
    this.user = user;
    this.ingredients = new ArrayList<IngredientRecipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(null, title, url, user, date, rating);
  }

  public Recipe(User user) {
    this.setStatus(Status.DIRTY);
    this.user = user;
    this.ingredients = new ArrayList<IngredientRecipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(null, null, null, user, null, null);
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
    IngredientRecipe ingredient_recipe = new IngredientRecipe((Long) this.getValue("rid"), ingredient, amount, unit);
    this.ingredients.add(ingredient_recipe);
  }

  public void addAllIngredients(List<IngredientRecipe> ingredientRecipes) {
    ingredients.addAll(ingredientRecipes);
  }

  public void addAllInstructions(List<Instruction> instructions) {
    instructions.addAll(instructions);
  }

  public void removeIngredient(Ingredient ingredient) {
    for (IngredientRecipe i : this.ingredients) {
      if (i.equals(ingredient)) {
        i.setStatus(Status.DELETED_LOCALLY);
      }
    }
  }

  public User getUser() {
    return user;
  }

  public List<IngredientRecipe> getIngredients() {
    return ingredients;
  }

  public List<Instruction> getInstructions() {
    return instructions;
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
