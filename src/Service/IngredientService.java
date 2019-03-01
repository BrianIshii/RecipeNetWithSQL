package Service;

import Entity.Ingredient;
import com.mysql.jdbc.Statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredientService extends EntityService {
  private static final String TABLE_NAME = "Ingredient";
  private static final String TABLE_ALIAS = "I";

  private static IngredientService instance = new IngredientService();

  private IngredientService() {}

  public static IngredientService getInstance() {
    return instance;
  }

  public Ingredient save(Ingredient ingredient) {
    if(ingredient.getIid() == null || ingredient.getIid() == 0) {
      Long iid = create(ingredient.getName());
      if (iid > 0) {
        ingredient.setIid(iid);
        return ingredient;
      }
    }
    //TODO add update case
    return null;
  }

  private Long create(String name) {
    String ingredientAdd =
        String.format("INSERT INTO %s (name) VALUES (?);", getTableName());
    try {
      PreparedStatement ps = con.prepareStatement(ingredientAdd, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, name);

      int affectedRows = ps.executeUpdate();

      if(affectedRows == 0) {
        return -1L;
      }

      return getKey(ps);
    } catch (SQLException sqle) {
      if(sqle.getMessage().contains("Duplicate")) {
        return 0L;
      }
      sqle.printStackTrace();
      return -1L;
    }
  }

  //TODO add update(Ingredient)

  public List<Ingredient> getAllIngredients() {
     String getAll = String.format("SELECT * FROM %s;", getTableName());
    try {
      PreparedStatement ps = con.prepareStatement(getAll);
      ResultSet rs = ps.executeQuery();
      ArrayList<Ingredient> ingredients = new ArrayList<>();
      Long id;
      String name;
      while (rs.next()) {
        id = rs.getLong(1);
        name = rs.getString(2);
        ingredients.add(new Ingredient(id, name));
      }
      return ingredients;
    } catch (SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  public String getTableName() {
    return TABLE_NAME;
  }

  public String getTableAlias() {
    return TABLE_ALIAS;
  }
}
