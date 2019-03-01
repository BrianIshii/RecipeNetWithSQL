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
    addField(Long.class, "rid", rid, true);
    addField(Long.class, "iid", ingredient.getValue("iid"), true);
    addField(Integer.class, "amount", amount, false);
    addField(String.class, "unit", unit, false);
  }

  public Ingredient_Recipe(Ingredient ingredient, Integer amount, String unit) {
    this.setStatus(Status.NEW);
    this.ingredient = ingredient;
    addField(Long.class, "rid", 0L, true);
    addField(Long.class, "iid", ingredient.getValue("iid"), true);
    addField(Integer.class, "amount", amount, false);
    addField(String.class, "unit", unit, false);
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
