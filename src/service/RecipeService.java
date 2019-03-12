package service;

import entity.*;
import schema.ResponseSchema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeService extends EntityService {
    private static RecipeService instance = new RecipeService();

    private RecipeService() {
    }

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
        List<ResponseSchema> response =
                executorService.executeSelect(Recipe.TABLE_NAME, Recipe.ENTITY_FIELDS, user.getField("uid"));
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
    public Recipe searchById(Long rid) {
        Recipe foundRecipe = new Recipe(rid);
        ResponseSchema response =
                executorService
                        .executeSelect(Recipe.TABLE_NAME, Recipe.ENTITY_FIELDS, foundRecipe.getPrimaryFields())
                        .get(0);
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
     * Saves both a recipe and any updates made to the children. Deletes instructions prior to saving
     * new ones to simplify things.
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
                    .forEach(i -> instructionService.delete(i)); // Delete its instructions
            if (super.delete(recipe)) return null;
            else
                throw new RuntimeException(
                        String.format(
                                "Recipe with title %s was marked for deletion but was not successfully deleted from the database.",
                                recipe.getFieldValue("title")));
        } else {
            // Save any updates to children and cleanses lists of deleted values
            recipe.getIngredients().removeIf(ir -> ingredientRecipeService.save(ir) == null);
            instructionService.clearRecipeInstructions((Long) recipe.getFieldValue("rid"));
            int step = 1;
            for (Iterator<Instruction> iterator = recipe.getInstructions().iterator();
                 iterator.hasNext(); ) {
                Instruction i = iterator.next();
                i.setFieldValue("step", step);
                instructionService.save(i);
            }
            return super.save(recipe);
        }
    }

    /**
     * Finds all recipes in the database. Does not load children. (LAZY)
     *
     * @return
     */
    public List<Recipe> searchAll() {
        List<ResponseSchema> response =
                executorService.executeSelect(Recipe.TABLE_NAME, Recipe.ENTITY_FIELDS);

        List<Recipe> recipes = new ArrayList<>();
        Recipe temp;
        for (ResponseSchema res : response) {
            temp = new Recipe(0L); // Another dummy initializer
            res.applyValuesTo(temp, true);
            temp.setSynced();
            recipes.add(temp);
        }

        return recipes;
    }
}
