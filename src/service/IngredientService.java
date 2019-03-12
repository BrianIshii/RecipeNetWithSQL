package service;

import entity.Ingredient;
import schema.ResponseSchema;

import java.util.ArrayList;
import java.util.List;

public class IngredientService extends EntityService {
  private static IngredientService instance = new IngredientService();

  private IngredientService() {}

  public static IngredientService getInstance() {
    return instance;
  }

  public Ingredient searchByName(String name) {
    Ingredient ingredient = new Ingredient(name);
      List<ResponseSchema> response =
            executorService.executeSelect(Ingredient.TABLE_NAME,
                    Ingredient.ENTITY_FIELDS, ingredient.getField("name"));
      if (response.size() == 0) {
      System.out.println(String.format("Could not find ingredient with name: %s", name));
      return null;
    }
      response.get(0).applyValuesTo(ingredient, true);
      ingredient.setSynced();
    return  ingredient;
  }

  public List<Ingredient> searchAll() {
      List<ResponseSchema> response =
            executorService.executeSelect(Ingredient.TABLE_NAME,
                    Ingredient.ENTITY_FIELDS);
    List<Ingredient> ingredients = new ArrayList<>();
      Ingredient temp;
      for (ResponseSchema res : response) {
      temp = new Ingredient();
          res.applyValuesTo(temp, true);
          temp.setSynced();
      ingredients.add(temp);
    }
    return ingredients;
  }
}
