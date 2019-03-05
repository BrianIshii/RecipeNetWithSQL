package ServiceTests;

import Entity.*;
import Service.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
                NAME, EMAIL, PASSWORD));
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
    clearRecipes();
    String template =
        "INSERT INTO %s (title, url, uid, date, rating) VALUES ('New Recipe', 'URL', 1, '2019-01-01', 5)";
    PreparedStatement ps = con.prepareStatement(String.format(template, Recipe.TABLE_NAME));
    ps.executeUpdate();
    ps.close();
  }
}
