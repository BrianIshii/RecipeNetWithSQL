package ServiceTests;

import Entity.Ingredient;
import Entity.Recipe;
import Entity.Status;
import Entity.User;
import Service.IngredientService;
import Service.RecipeService;
import Service.UserService;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
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
      Util.initUsers();
    }

    @Test
    void saveUser() throws SQLException {
      Util.clearUsers();
      user = userService.save(new User(NAME, EMAIL, PASSWORD));
      assertNotNull(user);
      assertNotNull(user.getValue("uid"));
      assertEquals(NAME, user.getValue("name"));
      assertEquals(EMAIL, user.getValue("email"));
      assertEquals(PASSWORD, user.getValue("password"));
    }
    @Test
    void authenticateUser() {
      user = userService.authenticate(EMAIL, PASSWORD);
      assertNotNull(user);
      assertEquals(Status.SYNCED, user.getStatus());
      assertNotNull(user.getValue("uid"));
      assertEquals(NAME, user.getValue("name"));
      assertEquals(EMAIL, user.getValue("email"));
      assertEquals(PASSWORD, user.getValue("password"));
    }

    @Test
    void updateUser() {
      final String newName = "rubbish";
      user = userService.authenticate(EMAIL, PASSWORD);
      assertNotNull(user, "Able to authenticate user");
      user.setValue("name", newName);
      assertEquals(Status.DIRTY, user.getStatus());
      user = userService.save(user);
      assertEquals(Status.SYNCED, user.getStatus());

      // Make sure the changes showed up
      user = userService.authenticate(EMAIL, PASSWORD);
      assertNotNull(user);
      assertNotNull(user.getValue("uid"));
      assertEquals(newName, user.getValue("name"));
      assertEquals(EMAIL, user.getValue("email"));
      assertEquals(PASSWORD, user.getValue("password"));
    }

    @Test
    void deleteUser() {
      user = userService.authenticate(EMAIL, PASSWORD);
      assertNotNull(user);
      assertTrue(userService.delete(user));
    }

    @Test
    void saveLocallyDeletedUser() {
      user = userService.authenticate(EMAIL, PASSWORD);
      user.setStatus(Status.DELETED_LOCALLY);
      assertNull(userService.save(user));
    }
  }

  @Nested
  @DisplayName("Ingredient Service")
  class IngredientServiceTest {
    @BeforeEach
    void beforeEach() throws SQLException{
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
      for(Ingredient i : ingredients) {
        assertEquals(Status.SYNCED, i.getStatus());
      }
    }

    @Test
    void deleteIngredient() {
      //Load an ingredient
      Ingredient ingredient = ingredientService.searchByName(INGREDIENTS[1]);
      assertNotNull(ingredient, "Ingredient was found");

      assertTrue(ingredientService.delete(ingredient));
    }
  }

  @Nested
  @DisplayName("Recipe Tests")
  class RecipeServiceTest {
    @BeforeEach
    void beforeEach() throws SQLException{
      Util.initIngredients();
      Util.initUsers();
      Util.initRecipes();
    }

    @Test
    void searchByUser() {
      User user = new User(NAME, EMAIL, PASSWORD);
      user = userService.save(user);
      assertNotNull(user);

      List<Recipe> recipes = recipeService.searchByUser(user);
      assertNotNull(recipes);
      assertEquals(1, recipes.size());
      Recipe recipe = recipes.get(0);
      assertEquals(Status.SYNCED, recipe.getStatus());
      assertEquals("New Recipe", recipe.getValue("title"));
      assertNotNull(recipe.getValue("rid"));
      assertNotNull(recipe.getValue("uid"));
      assertNotNull(recipe.getValue("date"));
      assertEquals(5, recipe.getValue("rating"));
    }
  }

//  @Nested
//  @DisplayName("Istruction Unit Tests")
//  class InstructionServiceTest {
//    @BeforeEach
//    void beforeEach() {
//
//    }
//  }
}
