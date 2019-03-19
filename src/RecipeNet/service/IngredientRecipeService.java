package RecipeNet.service;

import RecipeNet.entity.Ingredient;
import RecipeNet.entity.IngredientRecipe;
import RecipeNet.entity.Instruction;
import RecipeNet.entity.Recipe;
import RecipeNet.exception.DuplicateEntryException;
import RecipeNet.exception.ExecutorException;
import RecipeNet.exception.NoRowsAffectedException;
import RecipeNet.schema.Field;
import RecipeNet.schema.RequestSchema;
import RecipeNet.schema.ResponseSchema;
import RecipeNet.schema.Schema;

import java.util.LinkedList;
import java.util.List;

public class IngredientRecipeService extends EntityService {
  private static final String RECIPE_INGREDIENTS_VIEW = "IngredientsInRecipe";

  private static IngredientRecipeService instance = new IngredientRecipeService();
    private static IngredientService ingredientService = IngredientService.getInstance();

  public IngredientRecipeService() {}

  public static IngredientRecipeService getInstance() {
    return instance;
  }

  /**
   * Searches for all the IngredientRecipe rows that are associated with the given rid.
   *
   * @param rid
   * @return
   * @throws ExecutorException
   */
  public List<IngredientRecipe> searchByRecipe(Long rid) throws ExecutorException {
    // A list of fields that can be expected from the view
    RequestSchema expected =
        new RequestSchema()
            .addField(Long.class, Recipe.RID, null, true)
            .addField(Long.class, Ingredient.IID, null, true)
            .addField(String.class, Ingredient.NAME, null, false)
            .addField(Integer.class, IngredientRecipe.AMOUNT, null, false)
            .addField(String.class, IngredientRecipe.UNIT, null, false);

    // Key to use in the where clause
    Field searchKey = new Field<Long>(Long.class, Recipe.RID, rid, true);

    List<ResponseSchema> response =
        executorService.executeSelect(RECIPE_INGREDIENTS_VIEW, expected, searchKey);

    List<IngredientRecipe> ingredients = new LinkedList<>();
    IngredientRecipe tempIngredientRecipe;
    Ingredient tempIngredient;
    for (ResponseSchema res : response) {
      tempIngredient = new Ingredient(rid);
      res.applyValuesTo(tempIngredient, false);
      tempIngredient.setSynced();

      tempIngredientRecipe = new IngredientRecipe(rid, tempIngredient);
      res.applyValuesTo(tempIngredientRecipe, false);
      tempIngredientRecipe.setSynced();

      ingredients.add(tempIngredientRecipe);
    }

    return ingredients;
  }

  /**
   * Deletes all IngredientRecipe rows that contain the given rid.
   *
   * @param rid
   * @throws NoRowsAffectedException
   * @throws ExecutorException
   */
  public void deleteRecipeIngredients(Long rid) throws ExecutorException {
    executorService.executeDelete(
        IngredientRecipe.TABLE_NAME, new Field<>(Long.class, Instruction.RID, rid));
  }

  /**
   * Since this classes create method must be called, we need to create a save method to replace the
   * one from EntityService.
   *
   * @param ir
   * @return
   * @throws NoRowsAffectedException
   * @throws DuplicateEntryException
   * @throws ExecutorException
   */
  public IngredientRecipe save(IngredientRecipe ir) throws ExecutorException {
      //Must make sure the ingredient is up to date/exists prior to save
      ir.setIngredient(ingredientService.save(ir.getIngredient()));

      switch (ir.getStatus()) {
      case NEW:
        return create(ir);
      case DIRTY:
        return super.commit(ir);
      case SYNCED:
      default:
        return ir;
    }
  }

  /**
   * Needed to override the default save behavior. Normally, primary keys are extracted and
   * populated with generated keys. In the case of an IngredientRecipe, however, we know the primary
   * key prior to insertion.
   *
   * <p>Throws DuplicateEntryException.
   *
   * @param ingredientRecipe
   * @return
   * @throws DuplicateEntryException
   * @throws NoRowsAffectedException
   * @throws ExecutorException
   */
  private IngredientRecipe create(IngredientRecipe ingredientRecipe) throws ExecutorException {
    RequestSchema fields = new RequestSchema(ingredientRecipe.getFields());
    ResponseSchema response =
        executorService.executeInsert(IngredientRecipe.TABLE_NAME, new Schema(), fields);

    response.applyValuesTo(ingredientRecipe, true);
    ingredientRecipe.setSynced();
    return ingredientRecipe;
  }
}
