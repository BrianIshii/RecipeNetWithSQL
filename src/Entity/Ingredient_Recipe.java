package Entity;

public class Ingredient_Recipe extends Entity {
  public static final String TABLE_NAME = "Ingredient_Recipe";
  private Long rid;
  private Ingredient ingredient;
  private Integer amount;
  private String unit;

  public Ingredient_Recipe(Long rid, Ingredient ingredient, Integer amount, String unit) {
    this.setStatus(Status.DIRTY);
    this.ingredient = ingredient;
    initializeFields(rid, ingredient, amount, unit);
  }

  private void initializeFields(Long rid, Ingredient ingredient, Integer amount, String unit) {
    addField(Long.class, "rid", rid, true);
    addField(Long.class, "iid", ingredient.getValue("iid"), true);
    addField(Integer.class, "amount", amount, false);
    addField(String.class, "unit", unit, false);
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
