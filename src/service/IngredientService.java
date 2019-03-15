package service;

import entity.Ingredient;
import exception.EntityNotFoundException;
import exception.ExecutorException;
import schema.ResponseSchema;

import java.util.ArrayList;
import java.util.List;

public class IngredientService extends EntityService {
  private static IngredientService instance = new IngredientService();

  private IngredientService() {}

  public static IngredientService getInstance() {
    return instance;
  }

  /**
   * Searches the db for an ingredient with the exact given name.
   *
   * @param name
   * @return
   * @throws EntityNotFoundException
   * @throws ExecutorException
   */
  public Ingredient searchByName(String name) throws ExecutorException {
    Ingredient ingredient = new Ingredient(name);
    List<ResponseSchema> response =
        executorService.executeSelect(
            Ingredient.TABLE_NAME, Ingredient.ENTITY_FIELDS, ingredient.getField(Ingredient.NAME));
    if (response.size() == 0) throw new EntityNotFoundException();

    response.get(0).applyValuesTo(ingredient, true);
    ingredient.setSynced();
    return ingredient;
  }

  /**
   * Returns a list of all the Ingredients in the database.
   * @return
   * @throws ExecutorException
   */
  public List<Ingredient> searchAll() throws ExecutorException {
    List<ResponseSchema> response =
        executorService.executeSelect(Ingredient.TABLE_NAME, Ingredient.ENTITY_FIELDS);
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
