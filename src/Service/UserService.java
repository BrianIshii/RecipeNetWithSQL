package Service;

import Entity.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService extends EntityService {
  private static final String TABLE_NAME = "User";
  private static final String TABLE_ALIAS = "U";

  private static UserService instance = new UserService();

  private UserService() {}

  public static UserService getInstance() {
    return instance;
  }

  public User save(User user) {
    if (user.getUid() == null || user.getUid() == 0) {
      Long uid = create(user);
      if (uid > 0) {
        user.setUid(uid);
        return user;
      }
      System.err.println("Unable to create user");
    } else {
      if (update(user)) return user;
      System.err.println("Unable to update user");
    }
    return null;
  }

  /**
   * Authenticate a user using an email and password.
   *
   * @param email
   * @param pass
   * @return user with matching credentials, null otherwise
   */
  public User authenticate(String email, String pass) {
    String authString =
        String.format("SELECT * FROM %s WHERE email = ? AND password = ?", getTableName());
    try {
      PreparedStatement ps = con.prepareStatement(authString);
      ps.setString(1, email);
      ps.setString(2, pass);
      ResultSet rs = ps.executeQuery();

      if (!rs.next()) {
        return null;
      }
      Long id = rs.getLong(1);
      String name = rs.getString(2);
      return new User(id, name, email, pass);
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      return null;
    }
  }

  public boolean deleteByUID(Long uid) {
    return deleteByPK(getTableName(), User.PKName, uid);
  }

  private Long create(User user) {
    String userAdd =
        String.format(
            INSERT_TEMPLATE,
            getTableName(),
            composeSetFields(User.attributes),
            createBlankFields(User.attributes.size()));
    try {
      PreparedStatement ps = con.prepareStatement(userAdd);
      ps.setString(1, user.getName());
      ps.setString(2, user.getEmail());
      ps.setString(3, user.getPass());

      int affectedRows = ps.executeUpdate();

      if (affectedRows == 0) {
        return -1L;
      }

      return getKey(ps);
    } catch (SQLException sqle) {
      if (sqle.getMessage().contains("Duplicate entry")) {
        return 0L;
      }
      sqle.printStackTrace();
      return -1L;
    }
  }

  private boolean update(User user) {
    String userUpdate =
        String.format(UPDATE_TEMPLATE, getTableName(), composeCompareFields(User.attributes), User.PKName);
    try {
      PreparedStatement ps = con.prepareStatement(userUpdate);
      ps.setString(1, user.getName());
      ps.setString(2, user.getEmail());
      ps.setString(3, user.getPass());
      ps.setLong(4, user.getUid());

      return ps.execute();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      return false;
    }
  }


  public String getTableName() {
    return TABLE_NAME;
  }


  public String getTableAlias() {
    return TABLE_ALIAS;
  }
}
