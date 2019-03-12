package entity;

import schema.Schema;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Recipe extends Entity {
  public static final String TABLE_NAME = "Recipe";
  public static final String RID = "rid";
  public static final String TITLE = "title";
  public static final String URL = "url";
  public static final String UID = "uid";
  public static final String DATE = "date";
  public static final String RATING = "rating";
  public static final Schema ENTITY_FIELDS =
          new Schema()
          .addField(Long.class, RID, 0L, true)
          .addField(String.class, TITLE, null, false)
          .addField(String.class, URL, null, false)
          .addField(Long.class, UID, null, false)
          .addField(Date.class, DATE, null, false)
          .addField(Integer.class, RATING, null, false);

  private User user;
  private List<IngredientRecipe> ingredients;
  private List<Instruction> instructions;

  public Recipe(Long rid) {
    this.ingredients = new ArrayList<IngredientRecipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(rid, null, null, null, null, null);
    setDirty();
  }

  public Recipe(Long rid, String title, String url, User user, Date date, Integer rating) {
    this.user = user;
    this.ingredients = new ArrayList<IngredientRecipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(rid, title, url, user, date, rating);
    setDirty();
  }

  public Recipe(String title, String url, User user, Date date, Integer rating) {
    this.user = user;
    this.ingredients = new ArrayList<IngredientRecipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(null, title, url, user, date, rating);
  }

  public Recipe(User user) {
    this.user = user;
    this.ingredients = new ArrayList<IngredientRecipe>();
    this.instructions = new ArrayList<Instruction>();
    initializeFields(null, null, null, user, null, null);
    setDirty();
  }

  private void initializeFields(
      Long rid, String title, String url, User user, Date date, Integer rating) {
    Long uid = user == null ? null : (Long) user.getFieldValue("uid");
    fields = deepCopyFields(ENTITY_FIELDS);
    setFieldValue(RID, rid == null ? 0L : rid);
    setFieldValue(TITLE, title);
    setFieldValue(URL, url);
    setFieldValue(UID, user == null ? null : user.getFieldValue("uid"));
    setFieldValue(DATE, date);
    setFieldValue(RATING, rating);
    setNew();
  }

  public Ingredient getIngredient(Long iid) {
    for (IngredientRecipe ir : ingredients) {
      if (ir.getIngredient().getFieldValue("iid") == iid) {
        return ir.getIngredient();
      }
    }
    return null;
  }

  public void removeIngredient(Long iid) {
    for (IngredientRecipe ir : ingredients) {
      if (ir.getIngredient().getFieldValue("iid") == iid) {
        ir.setDeleted();
        return;
      }
    }
    setDirty();
  }

  public void addIngredient(Ingredient ingredient, Integer amount, String unit) {
    IngredientRecipe ingredient_recipe =
        new IngredientRecipe((Long) this.getFieldValue("rid"), ingredient, amount, unit);
    this.ingredients.add(ingredient_recipe);
    setDirty();
  }

  public void addAllIngredients(List<IngredientRecipe> ingredientRecipes) {
    ingredientRecipes.stream().forEach(i -> i.setFieldValue("rid", this.getFieldValue("rid")));
    this.ingredients.addAll(ingredientRecipes);
    setDirty();
  }

  public void addInstruction(String description) {
    Instruction instruction = new Instruction((Long) this.getFieldValue("rid"), -1, description);
    this.instructions.add(instruction);
    setDirty();
  }

  public void removeInstruction(Integer index) {
    this.instructions.remove(index);
    setDirty();
  }

  public void setInstruction(Integer index, String description) {
    this.instructions.get(index).setFieldValue("description", description);
    setDirty();
  }

  public void addAllInstructions(List<Instruction> instructions) {
    this.instructions.addAll(instructions);
    setDirty();
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
