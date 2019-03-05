package Service;

import Entity.Field;
import Entity.Ingredient;
import Entity.IngredientRecipe;
import Entity.Status;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class IngredientRecipeService extends EntityService {
  private static final String RECIPE_INGREDIENTS_VIEW = "RecipeInIngredients";

  private static IngredientRecipeService instance = new IngredientRecipeService();

  public IngredientRecipeService() {}

  public static IngredientRecipeService getInstance() {
    return instance;
  }

  public List<IngredientRecipe> searchByRecipe(Long rid) {
    // A list of fields that can be expected from the view
    List<Field> fieldsToExpect =
        Arrays.asList(
            new Field(Long.class, "rid", null, true),
            new Field(Long.class, "iid", null, true),
            new Field(String.class, "name", null, false),
            new Field(Integer.class, "amount", null, false),
            new Field(String.class, "unit", null, false));

    // Key to use in the where clause
    Field searchKey = new Field(Long.class, "rid", rid, true);

    List<List<Field>> fieldsExtracted =
        executorService.executeSelect(
            Ingredient.TABLE_NAME, fieldsToExpect, searchKey);

    List<IngredientRecipe> ingredients = new LinkedList<>();
    IngredientRecipe irTemp;
    Ingredient iTemp;
    for (List<Field> fieldGroup : fieldsExtracted) {
      iTemp = new Ingredient(rid);
      Field.applyTo(fieldGroup, iTemp.getFields(), false);
      iTemp.setStatus(Status.SYNCED);
      irTemp = new IngredientRecipe(rid, iTemp);
      Field.applyTo(fieldGroup, irTemp.getFields(), false);
      irTemp.setStatus(Status.SYNCED);
      ingredients.add(irTemp);
    }
    return ingredients;
  }
}
