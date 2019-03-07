package Service;

import Entity.Field;
import Entity.Ingredient;
import Entity.Status;

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
    List<List<Field>> extractedFields =
            executorService.executeSelect(Ingredient.TABLE_NAME,
                    ingredient.getFields(), ingredient.getField("name"));
    if (extractedFields.size() == 0) {
      System.out.println(String.format("Could not find ingredient with name: %s", name));
      return null;
    }
    Field.applyTo(extractedFields.get(0), ingredient.getFields(), true);

    ingredient.setStatus(Status.SYNCED);
    return  ingredient;
  }

  public List<Ingredient> searchAll() {
    Ingredient temp = new Ingredient();
    List<List<Field>> extractedFields =
            executorService.executeSelect(Ingredient.TABLE_NAME,
                    temp.getFields());
    List<Ingredient> ingredients = new ArrayList<>();
    for(List<Field> fieldGroup : extractedFields) {
      temp = new Ingredient();
      Field.applyTo(fieldGroup, temp.getFields(), true);
      temp.setStatus(Status.SYNCED);
      ingredients.add(temp);
    }

    return ingredients;
  }
}
