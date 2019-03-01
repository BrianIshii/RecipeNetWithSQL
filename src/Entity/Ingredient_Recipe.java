package Entity;

public class Ingredient_Recipe implements Entity {
  private Long rid;
  private Ingredient ingredient;
  private Integer amount;
  private String unit;

  public Ingredient_Recipe(Long rid, Ingredient ingredient, Integer amount, String unit) {
    this.rid = rid;
    this.ingredient = ingredient;
    this.amount = amount;
    this.unit = unit;
  }

  public Long getRid() {
    return rid;
  }

  public Long getIid() {
    return ingredient.getIid();
  }

  public Ingredient getIngredient() { return ingredient; }

  public Integer getAmount() {
    return amount;
  }

  public String getUnit() {
    return unit;
  }

  public void setRid(Long rid) {
    this.rid = rid;
  }

  public void setIngredient(Ingredient ingredient) {
    this.ingredient = ingredient;
  }
}
