package service;

import entity.Ingredient;
import entity.IngredientRecipe;
import formatter.Field;
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

    public List<IngredientRecipe> searchByRecipe(Long rid) {
        // A list of fields that can be expected from the view
        RequestSchema expected =
                new RequestSchema()
                        .addField(Long.class, "rid", null, true)
                        .addField(Long.class, "iid", null, true)
                        .addField(String.class, "name", null, false)
                        .addField(Integer.class, "amount", null, false)
                        .addField(String.class, "unit", null, false);

        // Key to use in the where clause
        Field searchKey = new Field(Long.class, "rid", rid, true);

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
