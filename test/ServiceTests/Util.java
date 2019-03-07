package ServiceTests;

import Entity.*;
import Service.DatabaseConnection;
import Service.UserService;
import Utilities.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static EntityTests.Constants.*;

public class Util {
  private static Connection con = DatabaseConnection.getInstance().getConnection();

  public static void clearDB() throws SQLException {
    clearIngredients();
    clearIngredientRecipes();
    clearInstructions();
    clearRecipes();
    clearUsers();
  }

  public static void clearUsers() throws SQLException {
    PreparedStatement ps =
        con.prepareStatement(String.format("DELETE FROM %s WHERE uid>0", User.TABLE_NAME));
    ps.execute();
    ps.close();
  }

  public static void clearIngredients() throws SQLException {
    PreparedStatement ps =
        con.prepareStatement(String.format("DELETE FROM %s WHERE iid>0", Ingredient.TABLE_NAME));
    ps.execute();
    ps.close();
  }

  public static void clearRecipes() throws SQLException {
    PreparedStatement ps =
        con.prepareStatement(String.format("DELETE FROM %s WHERE rid>0", Recipe.TABLE_NAME));
    ps.execute();
    ps.close();
  }

  public static void clearInstructions() throws SQLException {
    PreparedStatement ps =
        con.prepareStatement(String.format("TRUNCATE TABLE %s", Instruction.TABLE_NAME));
    ps.execute();
    ps.close();
  }

  public static void clearIngredientRecipes() throws SQLException {
    PreparedStatement ps =
        con.prepareStatement(String.format("TRUNCATE TABLE %s", IngredientRecipe.TABLE_NAME));
    ps.execute();
    ps.close();
  }

  public static void initUsers() throws SQLException {
    clearUsers();
    PreparedStatement ps =
        con.prepareStatement(
            String.format(
                "INSERT INTO User (name, email, password) VALUES ('%s', '%s', '%s');",
                USER_NAME, USER_EMAIL, USER_PASSWORD));
    ps.executeUpdate();
    ps.close();
  }

  public static void initIngredients() throws SQLException {
    clearIngredients();
    PreparedStatement ps;
    String template = "INSERT INTO %s (name) VALUES ('%s')";
    for (String i : INGREDIENTS) {
      ps = con.prepareStatement(String.format(template, Ingredient.TABLE_NAME, i));
      ps.executeUpdate();
      ps.close();
    }
  }

  public static void initRecipes() throws SQLException {
    clearInstructions();
    clearRecipes();

    initUsers();
    User user = UserService.getInstance().authenticate(USER_EMAIL, USER_PASSWORD);
    String date = DateUtils.dateToString(RECIPE_DATE);
    String query =
        String.format(
            "INSERT INTO %s (title, url, uid, date, rating) VALUES ('%s', '%s', %s, '%s', %s)",
            Recipe.TABLE_NAME, RECIPE_TITLE, RECIPE_URL, user.getValue("uid"), date, RECIPE_RATING);

    PreparedStatement ps = con.prepareStatement(query);
    ps.executeUpdate();
    ps.close();
  }

  public static void initInstructions(Long rid) throws SQLException {
    int step = 1;
    String query;
    for (String desc : INSTRUCTIONS) {
      query = String.format("INSERT INTO %s (rid, step, description) VALUES (%s, %s, '%s');", Instruction.TABLE_NAME,
              rid, step, desc);
      PreparedStatement ps = con.prepareStatement(query);
      ps.executeUpdate();
      ps.close();
      step++;
    }
  }

  public static void initIngredientRecipe(Long rid) throws SQLException {

  }
}
