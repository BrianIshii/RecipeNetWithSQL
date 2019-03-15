package service;

import entity.*;
import exception.EntityNotFoundException;
import exception.ExecutorException;
import exception.NoRowsAffectedException;
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
   * @throws ExecutorException
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
   * @throws EntityNotFoundException
   * @throws ExecutorException
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

  /**
   * Deletes a recipe and all of it's children.
   *
   * @param recipe
   * @throws ExecutorException
   */
  public void delete(Recipe recipe) throws ExecutorException {
    Long rid = (Long) recipe.getFieldValue(Recipe.RID);
    try {
      instructionService.deleteRecipeInstructions(rid);
    } catch (NoRowsAffectedException nraf) {
      // Recipe may not have had instructions
    }
    try {
      ingredientRecipeService.deleteRecipeIngredients(rid);
    } catch (NoRowsAffectedException nraf) {
      // Recipe may not have had ingredients
    }

    super.delete(recipe);
  }

  /**
   * Saves a Recipe and manages the state of all it's children.
   *
   * @param recipe
   * @return
   * @throws exception.DuplicateEntryException
   * @throws ExecutorException
   */
  public Recipe save(Recipe recipe) throws ExecutorException {
    Status recipeStartStatus = recipe.getStatus();
    Recipe temp = super.save(recipe);

    if (recipeStartStatus == Status.NEW) { // Need to apply generated rid prior to saving children
      temp.getIngredients().stream()
          .forEach(i -> i.setFieldValue(Recipe.RID, temp.getFieldValue(Recipe.RID)));
      temp.getInstructions().stream()
          .forEach(i -> i.setFieldValue(Recipe.RID, temp.getFieldValue(Recipe.RID)));
    }

    // Save any updates to children and cleanses lists of deleted values
    for (Iterator<IngredientRecipe> iterator = recipe.getIngredients().iterator();
        iterator.hasNext(); ) {
      IngredientRecipe ir = iterator.next();
      if (ir.getStatus() == Status.DELETED_LOCALLY) {
        try {
          ingredientRecipeService.delete(ir);
        } catch (EntityNotFoundException | NoRowsAffectedException ex) {
          // Don't really care. It didn't exist to begin with
        }
        iterator.remove();
      } else ingredientRecipeService.save(ir);
    }
    try {
      instructionService.deleteRecipeInstructions((Long) recipe.getFieldValue(Recipe.RID));
    } catch (EntityNotFoundException | NoRowsAffectedException ex) {
      //Recipe may not have had instructions to begin with
    }
    int step = 1;
    for (Iterator<Instruction> iterator = recipe.getInstructions().iterator();
        iterator.hasNext(); ) {
      Instruction i = iterator.next();
      i.setFieldValue("step", step);
      instructionService.save(i);
      step++;
    }
    return temp;
  }

  /**
   * Finds all recipes in the database. Does not load children. (LAZY)
   *
   * @return
   * @throws ExecutorException
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
