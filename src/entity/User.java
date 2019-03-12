package entity;

import schema.Schema;

public class User extends Entity {
  public static final String TABLE_NAME = "User";
  public static final Schema ENTITY_FIELDS =
          new Schema()
          .addField(Long.class, "uid", 0L, true)
          .addField(String.class, "name", null, false)
          .addField(String.class, "email", null, false)
          .addField(String.class, "password", null, false);

  public User() {
    initializeFields(null, null, null, null);
    this.setDirty();
  }

  public User(String email, String password) {
    initializeFields(null, null, email, password);
    setDirty();
  }

  public User(String name, String email, String password) {
    initializeFields(null, name, email, password);
  }

  public User(Long uid, String name, String email, String password) {
    initializeFields(uid, name, email, password);
  }

  private void initializeFields(Long uid, String name, String email, String password) {
    fields = deepCopyFields(ENTITY_FIELDS);
    setFieldValue("uid", uid == null ? 0L : uid);
    setFieldValue("name", name);
    setFieldValue("email", email);
    setFieldValue("password", password);
    setNew();
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
