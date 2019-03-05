package Entity;

public class User extends Entity {
  public static final String TABLE_NAME = "User";

  public User(String email, String password) {
    this.setStatus(Status.DIRTY);
    initializeFields(null, null, email, password);
  }

  public User(String name, String email, String password) {
    this.setStatus(Status.NEW);
    initializeFields(null, name, email, password);
  }

  public User(Long uid, String name, String email, String password) {
    this.setStatus(Status.DIRTY);
    initializeFields(uid, name, email, password);
  }

  private void initializeFields(Long uid, String name, String email, String password) {
    addField(Long.class, "uid", uid == null ? 0L : uid, true);
    addField(String.class, "name", name, false);
    addField(String.class, "email", email, false);
    addField(String.class, "password", password, false);
  }

  public String getTableName() {
    return TABLE_NAME;
  }

  public String toString() {
    return fields.toString();
  }
}
