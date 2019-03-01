package Service;

import Entity.Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class EntityService {
  protected Connection con = DatabaseConnection.getInstance().getConnection();

  protected static final String INSERT_TEMPLATE = "INSERT INTO %s %s VALUES %s";
  protected static final String UPDATE_TEMPLATE = "UPDATE %s SET %s WHERE %s=?";
  protected static final String DELETE_TEMPLATE = "DELETE FROM %s WHERE %s=?";

  static EntityService getInstance() {
    throw new RuntimeException("getInstance() has not been bound.");
  };


  public boolean deleteByPK(String tableName, String pkName, Long pk) {
    try {
      PreparedStatement ps =
          con.prepareStatement(String.format(DELETE_TEMPLATE, tableName, pkName));
      ps.setLong(1, pk);
      return ps.execute();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      return false;
    }
  }

}
