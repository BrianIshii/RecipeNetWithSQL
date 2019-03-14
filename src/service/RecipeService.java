package service;

import entity.*;
import exception.EntityNotFoundException;
import exception.ExecutorException;
import schema.ResponseSchema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeService extends EntityService {
  private static RecipeService instance = new RecipeService();

  private RecipeService() {}

  public static RecipeService getInstance() {
    return instance;
  }

  private IngredientRecipeService ingredientRecipeService = IngredientRecipeService.getInstance();
  private InstructionService instructionService = InstructionService.getInstance();

  /**
   * Finds all recipes for the given user. Will not load ingredients or instructions (LAZY).
   *
   * @param user
   * @return
   */
  public List<Recipe> searchByUser(User user) throws ExecutorException {
    List<ResponseSchema> response =
        executorService.executeSelect(
            Recipe.TABLE_NAME, Recipe.ENTITY_FIELDS, user.getField(User.UID));
    List<Recipe> recipes = new ArrayList<Recipe>();
    Recipe temp;
    for (ResponseSchema res : response) {
      temp = new Recipe(user);
      res.applyValuesTo(temp, true);
      temp.setSynced();
      recipes.add(temp);
    }
    return recipes;
  }

  /**
   * Finds a single recipe based on the rid. Will try to load all children (EAGER).
   *
   * @param rid
   * @return
   */
  public Recipe searchById(Long rid) throws ExecutorException {
    Recipe foundRecipe = new Recipe(rid);
    List<ResponseSchema> responses =
        executorService.executeSelect(
            Recipe.TABLE_NAME, Recipe.ENTITY_FIELDS, foundRecipe.getPrimaryFields());
    if (responses.size() == 0) throw new EntityNotFoundException();
    ResponseSchema response = responses.get(0);
    response.applyValuesTo(foundRecipe, true);

    // Get the ingredients
    List<IngredientRecipe> ingredientRecipes = ingredientRecipeService.searchByRecipe(rid);
    foundRecipe.addAllIngredients(ingredientRecipes);

    // Get the instructions
    List<Instruction> instructions = instructionService.searchByRecipe(rid);
    foundRecipe.addAllInstructions(instructions);

    foundRecipe.setSynced();
    return foundRecipe;
  }

  public void delete(Recipe recipe) throws ExecutorException {
    for (IngredientRecipe ir : recipe.getIngredients()) {
      ingredientRecipeService.delete(ir); // Delete connections to ingredients
    }
    for (Instruction i : recipe.getInstructions()) {
      instructionService.delete(i); // Delete its instructions
    }
    super.delete(recipe);
  }

  public Recipe save(Recipe recipe) throws ExecutorException {
    Status recipeStartStatus = recipe.getStatus();
    if (recipeStartStatus
        == Status.DELETED_LOCALLY) { // Intercept attempts to save locally deleted recipes
      delete(recipe);
      return null;
    }
    Recipe temp = super.save(recipe);

    if (recipeStartStatus == Status.NEW) { // Need to apply generated rid prior to save
      temp.getIngredients().stream()
          .forEach(i -> i.setFieldValue(Recipe.RID, temp.getFieldValue(Recipe.RID)));
      temp.getInstructions().stream()
          .forEach(i -> i.setFieldValue(Recipe.RID, temp.getFieldValue(Recipe.RID)));
    }

    // Save any updates to children and cleanses lists of deleted values
    for (Iterator<IngredientRecipe> iterator = recipe.getIngredients().iterator();
        iterator.hasNext(); ) {
      if (ingredientRecipeService.save(iterator.next()) == null) iterator.remove();
    }
    instructionService.clearRecipeInstructions((Long) recipe.getFieldValue(Recipe.RID));
    int step = 1;
    for (Iterator<Instruction> iterator = recipe.getInstructions().iterator();
        iterator.hasNext(); ) {
      Instruction i = iterator.next();
      i.setFieldValue("step", step);
      instructionService.save(i);
    }
    return temp;
  }

  /**
   * Finds all recipes in the database. Does not load children. (LAZY)
   *
   * @return
   */
  public List<Recipe> searchAll() throws ExecutorException {
    List<ResponseSchema> response =
        executorService.executeSelect(Recipe.TABLE_NAME, Recipe.ENTITY_FIELDS);

    List<Recipe> recipes = new ArrayList<>();
    Recipe temp;
    for (ResponseSchema res : response) {
      temp = new Recipe(0L); // Dummy initializer, rid will be overridden
      res.applyValuesTo(temp, true);
      temp.setSynced();
      recipes.add(temp);
    }

    return recipes;
  }
}
