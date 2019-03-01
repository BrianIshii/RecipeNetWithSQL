package Entity;

public class User extends Entity {
  public static final String TABLE_NAME = "User";

  public User(String name, String email, String password) {
    this.setStatus(Status.NEW);
    addField(Long.class, "uid", 0L, true);
    addField(String.class, "name", name, false);
    addField(String.class, "email", email, false);
    addField(String.class, "password", password, false);
  }

  public User(Long uid, String name, String email, String password) {
    this.setStatus(Status.DIRTY);
    addField(Long.class, "uid", uid, true);
    addField(String.class, "name", name, false);
    addField(String.class, "email", email, false);
    addField(String.class, "password", password, false);
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
