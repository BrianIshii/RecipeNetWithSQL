package Service;

import Entity.Ingredient_Recipe;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class IngredientRecipeService extends EntityService {
  private static final String TABLE_NAME = "Ingredient_Recipe";
  private static final String TABLE_ALIAS = "IR";

  private static IngredientRecipeService instance = new IngredientRecipeService();
  private static IngredientService ingredientService = IngredientService.getInstance();

  private IngredientRecipeService() {}

  public static IngredientRecipeService getInstance() {
    return instance;
  }

  public boolean save(Ingredient_Recipe ingredient_recipe) {
    // Trivial for now, but we may need to add update behavior
    return create(ingredient_recipe);
  }

  public boolean saveRecipeIngredients(List<Ingredient_Recipe> ingredient_recipeList) {
    for(Ingredient_Recipe ir : ingredient_recipeList) {
      if(ir.getIid() == 0) { //Save ingredient if it is new
        ir.setIngredient(ingredientService.save(ir.getIngredient()));
        if (ir.getIngredient() == null) return false;
      }
      //Save ingredientRecipe, return false if
      if(!save(ir))
        return false;
    }
    return true;
  }

  private boolean create(Ingredient_Recipe ir) {
    String irAdd =
        String.format(
            "INSERT INTO %s (rid, iid, amount, unit) VALUES (?, ?, ?, ?)", getTableName());
    try {
      PreparedStatement ps = con.prepareStatement(irAdd);
      ps.setLong(1, ir.getRid());
      ps.setLong(2, ir.getIid());
      ps.setInt(3, ir.getAmount());
      ps.setString(4, ir.getUnit());

      return ps.execute();
    } catch (SQLException sqle) {
      if (!sqle.getMessage().contains("Duplicate")) {
        sqle.printStackTrace();
        return false;
      }
      return true; //It already exists
    }
  }

  public String getTableName() {
    return TABLE_NAME;
  }

  public String getTableAlias() {
    return TABLE_ALIAS;
  }
}
