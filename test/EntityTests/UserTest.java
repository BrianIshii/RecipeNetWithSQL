package EntityTests;

import schema.Field;
import foo.Status;
import foo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static EntityTests.Constants.USER_EMAIL;
import static EntityTests.Constants.USER_NAME;
import static EntityTests.Constants.USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest{
  User user;

  @BeforeEach
  public void beforeEach() {
    user = new User(USER_NAME, USER_EMAIL, USER_PASSWORD);
  }

  @Test
  public void checkTableName() {
    assertEquals("User", User.TABLE_NAME);
  }

  @Test
  public void createUser() {
    assertNotNull(user);
    assertEquals(user.getStatus(), Status.NEW);
  }

  @Test
  public void checkValues() {
    assertEquals(USER_NAME, user.getFieldValue("name"));
    assertEquals(USER_EMAIL, user.getFieldValue("email"));
    assertEquals(USER_PASSWORD, user.getFieldValue("password"));
  }

  @Test
  public void checkPrimaryKey() {
    String pkname = user.getPrimaryKeys().get(0).getKey();
    assertEquals("uid", pkname);
  }

  @Test
  public void testSetValues() {
    user.setSynced();
    user.setFieldValue("name", "NEW");
    user.setFieldValue("email", "INVALID");
    assertEquals("NEW", user.getFieldValue("name"));
    assertEquals("INVALID", user.getFieldValue("email"));
    assertEquals(user.getStatus(), Status.DIRTY);
  }

  @Test
  public void testGetField() {
    Field field = new Field<>(String.class, "name", USER_NAME, false);
    assertEquals(field, user.getField("name"));
  }

  @Test
  public void testGetNonPrimaryFields() {
    List<Field> fields = user.getNonPrimaryKeys();
    assertEquals("name", fields.get(0).getKey());
    assertEquals("email", fields.get(1).getKey());
    assertEquals("password", fields.get(2).getKey());
  }

  @Test
  public void testGetFields() {
    List<Field> fields = user.getFields();
    assertEquals("uid", fields.get(0).getKey());
    assertEquals("name", fields.get(1).getKey());
    assertEquals("email", fields.get(2).getKey());
    assertEquals("password", fields.get(3).getKey());
  }

  @Test
  public void testMissingKey() {
    RuntimeException thrown = assertThrows(RuntimeException.class, () -> user.getField("bogus"));
    assertTrue(thrown.getMessage().contains("bogus"));
  }

  @Test
  public void testSetWrongValueType() {
    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> user.setFieldValue("uid", "STRING"));
    assertTrue(thrown.getMessage().contains("Attempting to assign field"));
  }

  @Test
  public void testIndependence() {
    User newUser = new User("who", "am", "I");
    user.setFieldValue("name", "BOGUS");
    assertNotEquals(newUser.getFieldValue("name"), user.getFieldValue("name"));
    assertNotEquals(newUser.getFieldValue("email"), user.getFieldValue("email"));
    assertNotEquals(newUser.getFieldValue("password"), user.getFieldValue("password"));
  }
}
