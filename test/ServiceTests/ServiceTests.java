package ServiceTests;

import foo.Ingredient;
import foo.Recipe;
import foo.Status;
import foo.User;
import org.junit.jupiter.api.*;
import foo2.IngredientService;
import foo2.RecipeService;
import foo2.UserService;
import utilities.DateUtils;

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
      assertNotNull(user.getFieldValue("uid"));
      assertEquals(USER_NAME, user.getFieldValue("name"));
      assertEquals(USER_EMAIL, user.getFieldValue("email"));
      assertEquals(USER_PASSWORD, user.getFieldValue("password"));
    }

    @Test
    void authenticateUser() {
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user);
      assertEquals(Status.SYNCED, user.getStatus());
      assertNotNull(user.getFieldValue("uid"));
      assertEquals(USER_NAME, user.getFieldValue("name"));
      assertEquals(USER_EMAIL, user.getFieldValue("email"));
      assertEquals(USER_PASSWORD, user.getFieldValue("password"));
    }

    @Test
    void updateUser() {
      final String newName = "rubbish";
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user, "Able to authenticate user");

      user.setFieldValue("name", newName);
      assertEquals(Status.DIRTY, user.getStatus());

      user = userService.save(user);
      assertEquals(Status.SYNCED, user.getStatus());

      // Make sure the changes showed up
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user);
      assertNotNull(user.getFieldValue("uid"));
      assertEquals(newName, user.getFieldValue("name"));
      assertEquals(USER_EMAIL, user.getFieldValue("email"));
      assertEquals(USER_PASSWORD, user.getFieldValue("password"));
    }

    @Test
    void deleteUser() {
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user);
      user.setDeleted();

      assertNull(userService.save(user));
    }

    @Test
    void saveLocallyDeletedUser() {
      user = userService.authenticate(USER_EMAIL, USER_PASSWORD);

      user.setDeleted();
      assertNull(userService.save(user));
    }

    @Test
    void fuzzyNameSearch() throws SQLException{
      List<User> results = userService.fuzzyNameSearch(USER_NAME + "1", 5);
      results.stream()
          .filter(u -> u.getFieldValue("name").equals(USER_NAME))
          .findAny()
          .orElseThrow(() -> new RuntimeException(String.format("Search did not include user with name %s", USER_NAME)));
    }

    @Test
    void fuzzyNameSearchMultiple() {
      //Add two extra users with similar names
      userService.save(new User(USER_NAME+"1", USER_EMAIL+"2", USER_PASSWORD));
      userService.save(new User(USER_NAME+"2", USER_EMAIL+"3", USER_PASSWORD));

      List<User> results = userService.fuzzyNameSearch(USER_NAME + "1", 5);
      assertEquals(3, results.size());
      results.stream()
              .filter(u -> u.getFieldValue("name").equals(USER_NAME))
              .findAny()
              .orElseThrow(() -> new RuntimeException(String.format("Search did not include user with name %s", USER_NAME)));
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
      assertEquals("Rice", ingredient.getFieldValue("name"));
    }

    @Test
    void seachByName() {
      String thisIngredient = INGREDIENTS[0];
      Ingredient ingredient = ingredientService.searchByName(thisIngredient);
      assertNotNull(ingredient);
      assertEquals(Status.SYNCED, ingredient.getStatus());
      assertEquals(thisIngredient, ingredient.getFieldValue("name"));
    }

    @Test
    void saveLocallyDeletedIngredient() {
      Ingredient ingredient = new Ingredient("Rice");
      ingredient.setDeleted();
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

      ingredient.setDeleted();

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
      assertEquals(RECIPE_TITLE, recipe.getFieldValue("title"));
      assertEquals(RECIPE_URL, recipe.getFieldValue("url"));
      assertEquals(user.getFieldValue("uid"), recipe.getFieldValue("uid"));
      assertEquals(
          DateUtils.dateToString(RECIPE_DATE),
          DateUtils.dateToString((Date) recipe.getFieldValue("date")));
      assertEquals(RECIPE_RATING, recipe.getFieldValue("rating"));
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
      assertEquals(RECIPE_TITLE, recipe.getFieldValue("title"));
      assertEquals(RECIPE_URL, recipe.getFieldValue("url"));
      assertNotNull(recipe.getFieldValue("uid"));
      assertEquals(
          DateUtils.dateToString(RECIPE_DATE),
          DateUtils.dateToString((Date) recipe.getFieldValue("date")));
      assertEquals(RECIPE_RATING, recipe.getFieldValue("rating"));
      assertEquals(0, recipe.getIngredients().size(), "Does not fetch ingredients");
      assertEquals(0, recipe.getIngredients().size(), "Does not fetch instructions");
    }

    @Test
    void saveRecipe() throws SQLException {
      User user = userService.authenticate(USER_EMAIL, USER_PASSWORD);
      assertNotNull(user);

      Util.clearInstructions();
      Util.clearRecipes();
      Recipe recipe = new Recipe(RECIPE_TITLE, RECIPE_URL, user, RECIPE_DATE, RECIPE_RATING);
    }

    @Test
    void searchByRid() throws SQLException {
      // Get a recipe to search by
      List<Recipe> recipes = recipeService.searchAll();
      assertNotNull(recipes);
      assertEquals(1, recipes.size(), "We have a recipe to test with");
      Recipe recipe = recipes.get(0);
      assertNotNull(recipe);
      Util.initInstructions((Long) recipe.getFieldValue("rid"));

      Recipe foundRecipe = recipeService.searchById((Long) recipe.getFieldValue("rid"));
      assertEquals(Status.SYNCED, foundRecipe.getStatus());
      assertNotNull(foundRecipe.getInstructions());
      assertEquals(INSTRUCTIONS.length, foundRecipe.getInstructions().size());
      // TODO check ingredients
    }
  }
}
