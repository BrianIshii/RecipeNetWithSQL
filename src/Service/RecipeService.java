package Service;

import Entity.*;

import java.util.ArrayList;
import java.util.Collection;
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
  public List<Recipe> searchByUser(User user) {
    Recipe temp = new Recipe(user);

    List<List<Field>> extractedFields =
        executorService.executeSelect(Recipe.TABLE_NAME, temp.getFields(), temp.getField("uid"));
    List<Recipe> recipes = new ArrayList<Recipe>();
    for (List<Field> fieldGroup : extractedFields) {
      temp = new Recipe(user);
      Field.applyTo(fieldGroup, temp.getFields(), true);
      temp.setStatus(Status.SYNCED);
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
  public Recipe searchById(Long rid) {
    Recipe foundRecipe = new Recipe(rid);
    List<Field> extractedFields =
        executorService
            .executeSelect(Recipe.TABLE_NAME, foundRecipe.getFields(), foundRecipe.getPrimaryKey())
            .get(0);
    Field.applyTo(extractedFields, foundRecipe.getFields(), true);

    // Get the ingredients
    List<IngredientRecipe> ingredientRecipes = ingredientRecipeService.searchByRecipe(rid);
    foundRecipe.addAllIngredients(ingredientRecipes);

    // Get the instructions
    List<Instruction> instructions = instructionService.searchByRecipe(rid);
    foundRecipe.addAllInstructions(instructions);

    foundRecipe.setStatus(Status.SYNCED);
    return foundRecipe;
  }

  /**
   * Saves both a recipe and any updates made to the children.
   *
   * @param recipe
   * @return
   */
  public Recipe save(Recipe recipe) {
    if (recipe.getStatus() == Status.DELETED_LOCALLY) { // If the recipe is deleted...
      recipe
          .getIngredients()
          .forEach(ir -> ingredientRecipeService.delete(ir)); // Delete connections to ingredients
      recipe
          .getInstructions()
          .forEach(i -> instructionService.delete(i)); // Delete its instructsions
      if (super.delete(recipe)) return null;
      else
        throw new RuntimeException(
            String.format(
                "Recipe with title %s was marked for deletion but was not successfully deleted from the database.",
                recipe.getValue("title")));
    } else {
      // Save any updates to children and cleanse lists of deleted values
      recipe.getIngredients().removeIf(ir -> ingredientRecipeService.save(ir) == null);
      recipe.getInstructions().removeIf(i -> instructionService.save(i) == null);
      return super.save(recipe);
    }
  }

  /**
   * Finds all recipes in the database. Does not load children. (LAZY)
   *
   * @return
   */
  public List<Recipe> searchAll() {
    Recipe temp = new Recipe(0L); // Object with dummy value
    List<List<Field>> extractedFields =
        executorService.executeSelect(Recipe.TABLE_NAME, temp.getFields());

    List<Recipe> recipes = new ArrayList<>();
    for (List<Field> fieldGroup : extractedFields) {
      temp = new Recipe(0L); // Another dummy initializer
      Field.applyTo(fieldGroup, temp.getFields(), true);
      temp.setStatus(Status.SYNCED);
      recipes.add(temp);
    }

    return recipes;
  }
}
