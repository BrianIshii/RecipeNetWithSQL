package Service;

import Entity.Recipe;
import com.mysql.jdbc.Statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RecipeService extends EntityService {
  private static final String TABLE_NAME = "Recipe";
  private static final String TABLE_ALIAS = "R";
  private static final String RECIPE_INGREDIENTS_VIEW = "RecipeIngredients";

  private static RecipeService instance = new RecipeService();
  private static IngredientService ingredientService = IngredientService.getInstance();
  private static IngredientRecipeService ingredientRecipeService =
      IngredientRecipeService.getInstance();
  private static InstructionService instructionService = InstructionService.getInstance();

  private RecipeService() {};

  public static RecipeService getInstance() {
    return instance;
  }

//  public Recipe save(Recipe recipe) {
//    if (recipe.getRid() == null || recipe.getRid() == 0) {
//      Long rid = create(recipe);
//      if (rid > 0) {
//        recipe.setRid(rid);
//        if (!ingredientRecipeService.saveRecipeIngredients(recipe.getIngredients()))
//          throw new RuntimeException("Unable to save ingredients");
//        if (!instructionService.saveIntructions(recipe.getInstructions()))
//          throw new RuntimeException(("Unable to save instructions"));
//
//        return recipe;
//      }
//    } else {
//      if (update(recipe)) return recipe;
//    }
//    return null;
//  }

  public Long create(Recipe recipe) {
    String recipeAdd =
        String.format(
            INSERT_TEMPLATE,
            getTableName(),
            composeSetFields(Recipe.attributes),
            createBlankFields(Recipe.attributes.size()));
    try {
      PreparedStatement ps = con.prepareStatement(recipeAdd, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, recipe.getTitle());
      ps.setString(2, recipe.getUrl());
      ps.setLong(3, recipe.getUid());
      ps.setDate(4, recipe.getDate());
      ps.setInt(5, recipe.getRating());

      int affectedRows = ps.executeUpdate();

      if (affectedRows == 0) {
        return -1L;
      }

      return getKey(ps);
    } catch (SQLException sqle) {
      if (!sqle.getMessage().contains("Duplicate")) {
        sqle.printStackTrace();
        return 0L;
      }
      return -1L;
    }
  }

  public boolean update(Recipe recipe) {
    String recipeUpdate =
        String.format(
            UPDATE_TEMPLATE,
            getTableName(),
            composeCompareFields(Recipe.attributes),
            Recipe.PKName);
    try {
      PreparedStatement ps = con.prepareStatement(recipeUpdate);
      ps.setString(1, recipe.getTitle());
      ps.setString(2, recipe.getTitle());
      ps.setLong(3, recipe.getUid());
      ps.setDate(4, recipe.getDate());
      ps.setInt(5, recipe.getRating());
      ps.setLong(6, recipe.getRid());

      return ps.execute();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      return false;
    }
  }

  public String getTableName() {
    return TABLE_NAME;
  }

  public String getTableAlias() {
    return TABLE_ALIAS;
  }
}
