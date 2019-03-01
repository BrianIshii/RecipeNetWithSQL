package Entity;

import javax.lang.model.element.ElementKind;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recipe implements Entity {
  public static final List<String> attributes =
      Arrays.asList("title", "url", "uid", "date", "rating");
  public static final String PKName = "rid";
  private Long rid;
  private String title;
  private String url;
  private Long uid;
  private Date date;
  private Integer rating;
  private List<Ingredient_Recipe> ingredients;
  private List<Instruction> instructions;

  public Recipe(Long rid, String title, String url, Long uid, Date date, Integer rating) {
    this.rid = rid;
    this.title = title;
    this.url = url;
    this.uid = uid;
    this.date = date;
    this.rating = rating;
    this.ingredients = new ArrayList<Ingredient_Recipe>();
    this.instructions = new ArrayList<Instruction>();
  }

  public Recipe(String title, String url, Long uid, Date date, Integer rating) {
    this.rid = null;
    this.title = title;
    this.url = url;
    this.uid = uid;
    this.date = date;
    this.rating = rating;
    this.ingredients = new ArrayList<Ingredient_Recipe>();
    this.instructions = new ArrayList<Instruction>();
  }

  public void addIngredient(Ingredient ingredient, Integer amount, String unit) {
    ingredients.add(new Ingredient_Recipe(this.rid, ingredient, amount, unit));
  }

  public void addInstruction(Instruction instruction) {
    instruction.setRid(this.rid);
    instructions.add(instruction);
  }

  public Long getRid() {
    return rid;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public Long getUid() {
    return uid;
  }

  public Date getDate() {
    return date;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRid(Long rid) {
    this.rid = rid;
  }

  public List<Ingredient_Recipe> getIngredients() {
    return ingredients;
  }

  public List<Instruction> getInstructions() {
    return instructions;
  }
}
