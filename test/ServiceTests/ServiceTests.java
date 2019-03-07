package ServiceTests;

import Entity.Ingredient;
import Entity.Recipe;
import Entity.Status;
import Entity.User;
import Service.IngredientService;
import Service.RecipeService;
import Service.UserService;
import Utilities.DateUtils;
import org.junit.jupiter.api.*;

import javax.sound.midi.SysexMessage;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static EntityTests.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
  private static UserService userService;
  private static IngredientService ingredientService;
  private static RecipeService recipeService;
  private static User user;

  @BeforeAll
  static void beforeAll() throws SQLException {
    userService = UserService.getInstance();
    ingredientService = IngredientService.getInstance();
    recipeService = RecipeService.getInstance();
    Util.clearDB();
  }

  @Nested
  @DisplayName("User Service")
  class UserServiceTest {
    @BeforeEach
    void beforeEach() throws SQLException {
      Util.clearDB();
      Util.initUsers();
    }

    @Test
    void saveUser() throws SQLException {
      Util.clearUsers();

      user = userService.save(new User(USER_NAME, USER_EMAIL, USER_PASSWORD));
      assertNotNull(user);
      assertNotNull(user.getValue("uid"));
      assertEquals(USER_NAME, user.getValue("name"));
      assertEquals(USER_EMAIL, user.getValue("email"));
      assertEquals(USER_PASSWORD, user.getValue("password"));
    }

    @Test
    void authenticateUser() {
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user);
      assertEquals(Status.SYNCED, user.getStatus());
      assertNotNull(user.getValue("uid"));
      assertEquals(USER_NAME, user.getValue("name"));
      assertEquals(USER_EMAIL, user.getValue("email"));
      assertEquals(USER_PASSWORD, user.getValue("password"));
    }

    @Test
    void updateUser() {
      final String newName = "rubbish";
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user, "Able to authenticate user");

      user.setValue("name", newName);
      assertEquals(Status.DIRTY, user.getStatus());

      user = userService.save(user);
      assertEquals(Status.SYNCED, user.getStatus());

      // Make sure the changes showed up
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user);
      assertNotNull(user.getValue("uid"));
      assertEquals(newName, user.getValue("name"));
      assertEquals(USER_EMAIL, user.getValue("email"));
      assertEquals(USER_PASSWORD, user.getValue("password"));
    }

    @Test
    void deleteUser() {
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user);
      user.setStatus(Status.DELETED_LOCALLY);

      assertNull(userService.save(user));
    }

    @Test
    void saveLocallyDeletedUser() {
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);

      user.setStatus(Status.DELETED_LOCALLY);
      assertNull(userService.save(user));
    }
  }

  @Nested
  @DisplayName("Ingredient Service")
  class IngredientServiceTest {
    @BeforeEach
    void beforeEach() throws SQLException {
      Util.initIngredients();
    }

    @Test
    void saveIngredient() {
      Ingredient ingredient = new Ingredient("Rice");
      ingredient = ingredientService.save(ingredient);
      assertNotNull(ingredient);
      assertEquals(Status.SYNCED, ingredient.getStatus());
      assertEquals("Rice", ingredient.getValue("name"));
    }

    @Test
    void seachByName() {
      String thisIngredient = INGREDIENTS[0];
      Ingredient ingredient = ingredientService.searchByName(thisIngredient);
      assertNotNull(ingredient);
      assertEquals(Status.SYNCED, ingredient.getStatus());
      assertEquals(thisIngredient, ingredient.getValue("name"));
    }

    @Test
    void saveLocallyDeletedIngredient() {
      Ingredient ingredient = new Ingredient("Rice");
      ingredient.setStatus(Status.DELETED_LOCALLY);
      ingredient = ingredientService.save(ingredient);
      assertNull(ingredient);
    }

    @Test
    void searchAllIngredients() {
      List<Ingredient> ingredients = ingredientService.searchAll();
      assertEquals(INGREDIENTS.length, ingredients.size());
      for (Ingredient i : ingredients) {
        assertEquals(Status.SYNCED, i.getStatus());
      }
    }

    @Test
    void deleteIngredient() {
      // Load an ingredient
      Ingredient ingredient = ingredientService.searchByName(INGREDIENTS[1]);
      assertNotNull(ingredient, "Ingredient was found");

      ingredient.setStatus(Status.DELETED_LOCALLY);

      assertNull(ingredientService.save(ingredient));
    }
  }

  @Nested
  @DisplayName("Recipe Tests")
  class RecipeServiceTest {
    @BeforeEach
    void beforeEach() throws SQLException {
      Util.initIngredients();
      Util.initRecipes();
    }

    @Test
    void searchByUser() {
      User user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user);

      List<Recipe> recipes = recipeService.searchByUser(user);
      assertNotNull(recipes);
      assertEquals(1, recipes.size());
      Recipe recipe = recipes.get(0);
      assertEquals(Status.SYNCED, recipe.getStatus());
      assertEquals(RECIPE_TITLE, recipe.getValue("title"));
      assertEquals(RECIPE_URL, recipe.getValue("url"));
      assertEquals(user.getValue("uid"), recipe.getValue("uid"));
      assertEquals(
          DateUtils.dateToString(RECIPE_DATE),
          DateUtils.dateToString((Date) recipe.getValue("date")));
      assertEquals(RECIPE_RATING, recipe.getValue("rating"));
      assertEquals(0, recipe.getIngredients().size(), "Does not fetch ingredients");
      assertEquals(0, recipe.getIngredients().size(), "Does not fetch instructions");
    }

    @Test
    void searchAll() {
      List<Recipe> recipes = recipeService.searchAll();
      assertNotNull(recipes);
      assertEquals(1, recipes.size());
      Recipe recipe = recipes.get(0);

      assertEquals(Status.SYNCED, recipe.getStatus());
      assertEquals(RECIPE_TITLE, recipe.getValue("title"));
      assertEquals(RECIPE_URL, recipe.getValue("url"));
      assertNotNull(recipe.getValue("uid"));
      assertEquals(
              DateUtils.dateToString(RECIPE_DATE),
              DateUtils.dateToString((Date) recipe.getValue("date")));
      assertEquals(RECIPE_RATING, recipe.getValue("rating"));
      assertEquals(0, recipe.getIngredients().size(), "Does not fetch ingredients");
      assertEquals(0, recipe.getIngredients().size(), "Does not fetch instructions");
    }

    @Test
    void saveRecipe() throws SQLException{
      User user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user);

      Util.clearInstructions();
      Util.clearRecipes();
      Recipe recipe = new Recipe(RECIPE_TITLE, RECIPE_URL, user, RECIPE_DATE, RECIPE_RATING);
    }

    @Test
    void searchByRid() throws SQLException {
      //Get a recipe to search by
      List<Recipe> recipes = recipeService.searchAll();
      assertNotNull(recipes);
      assertEquals(1, recipes.size(), "We have a recipe to test with");
      Recipe recipe = recipes.get(0);
      assertNotNull(recipe);
      Util.initInstructions((Long)recipe.getValue("rid"));

      Recipe foundRecipe = recipeService.searchById((Long)recipe.getValue("rid"));
      assertEquals(Status.SYNCED, foundRecipe.getStatus());
      assertNotNull(foundRecipe.getInstructions());
      assertEquals(INSTRUCTIONS.length, foundRecipe.getInstructions().size());
      //TODO check ingredients
    }
  }
}
