package service;

import entity.Ingredient;
import entity.IngredientRecipe;
import entity.Instruction;
import entity.Recipe;
import exception.ExecutorException;
import schema.Field;
import schema.RequestSchema;
import schema.ResponseSchema;

import java.util.LinkedList;
import java.util.List;

public class IngredientRecipeService extends EntityService {
    private static final String RECIPE_INGREDIENTS_VIEW = "IngredientsInRecipe";

    private static IngredientRecipeService instance = new IngredientRecipeService();

    public IngredientRecipeService() {
    }

    public static IngredientRecipeService getInstance() {
        return instance;
    }

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
}
