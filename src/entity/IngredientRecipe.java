package entity;

import schema.Schema;

public class IngredientRecipe extends Entity {
  public static final String TABLE_NAME = "Ingredient_Recipe";
  public static final String RID = "rid";
  public static final String IID = "iid";
  public static final String AMOUNT = "amount";
  public static final String UNIT = "unit";
  public static final Schema ENTITY_FIELDS =
      new Schema()
          .addField(Long.class, RID, 0L, true)
          .addField(Long.class, IID, 0L, true)
          .addField(Integer.class, AMOUNT, false)
          .addField(String.class, UNIT, false);

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
    setFieldValue(RID, rid);
    setFieldValue(IID, ingredient.getFieldValue("iid"));
    setFieldValue(AMOUNT, amount);
    setFieldValue(UNIT, unit);
    setNew();
  }

  public Ingredient getIngredient() {
    return ingredient;
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
