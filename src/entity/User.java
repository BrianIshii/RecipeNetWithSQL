package entity;

import schema.Schema;

public class User extends Entity {
  public static final String TABLE_NAME = "User";
  public static final String UID = "uid";
  public static final String NAME = "name";
  public static final String EMAIL = "email";
  public static final String PASSWORD = "password";
  public static final Schema ENTITY_FIELDS =
          new Schema()
          .addField(Long.class, UID, 0L, true)
          .addField(String.class, NAME, null, false)
          .addField(String.class, EMAIL, null, false)
          .addField(String.class, PASSWORD, null, false);

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
    setFieldValue(UID, uid == null ? 0L : uid);
    setFieldValue(NAME, name);
    setFieldValue(EMAIL, email);
    setFieldValue(PASSWORD, password);
    setNew();
  }

  public String getTableName() {
    return TABLE_NAME;
  }
}
