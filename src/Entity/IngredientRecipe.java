package entity;

import schema.Schema;

public class IngredientRecipe extends Entity {
  public static final String TABLE_NAME = "Ingredient_Recipe";
  public static final Schema ENTITY_FIELDS =
          new Schema()
          .addField(Long.class, "rid", 0L, true)
          .addField(Long.class, "iid", 0L, true)
          .addField(Integer.class, "amount", null, false)
          .addField(String.class, "unit", null, false);


  private Ingredient ingredient;

  public IngredientRecipe(Long rid, Ingredient ingredient, Integer amount, String unit) {
    this.ingredient = ingredient;
    initializeFields(rid, ingredient, amount, unit);
    setDirty();
  }

  public IngredientRecipe(Long rid, Ingredient ingredient) {
    this.ingredient = ingredient;
    initializeFields(rid, ingredient, null, null);
  }

  private void initializeFields(Long rid, Ingredient ingredient, Integer amount, String unit) {
    fields = deepCopyFields(ENTITY_FIELDS);
    setFieldValue("rid", rid);
    setFieldValue("iid", ingredient.getFieldValue("iid"));
    setFieldValue("amount", amount);
    setFieldValue("unit", unit);
    setNew();
  }

  public Ingredient getIngredient() {
    return ingredient;
  }

  public Ingredient getIngredient() {
    return ingredient;
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
