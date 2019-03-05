package EntityTests;

import Entity.Field;
import Entity.Status;
import Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static EntityTests.Constants.EMAIL;
import static EntityTests.Constants.NAME;
import static EntityTests.Constants.PASSWORD;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest{
  User user;

  @BeforeEach
  public void beforeEach() {
    user = new User(NAME, EMAIL, PASSWORD);
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
    assertEquals(NAME, user.getValue("name"));
    assertEquals(EMAIL, user.getValue("email"));
    assertEquals(PASSWORD, user.getValue("password"));
  }

  @Test
  public void checkPrimaryKey() {
    String pkname = user.getPrimaryKey().get(0).getKey();
    assertEquals("uid", pkname);
  }

  @Test
  public void testSetValues() {
    user.setStatus(Status.SYNCED);
    user.setValue("name", "NEW");
    user.setValue("email", "INVALID");
    assertEquals("NEW", user.getValue("name"));
    assertEquals("INVALID", user.getValue("email"));
    assertEquals(user.getStatus(), Status.DIRTY);
  }

  @Test
  public void testGetField() {
    Field field = new Field<>(String.class, "name", NAME, false);
    field.setColumn(2);
    assertEquals(field, user.getField("name"));
  }

  @Test
  public void testGetNonPrimaryFields() {
    List<Field> fields = user.getNonPrimaryFields();
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
        assertThrows(RuntimeException.class, () -> user.setValue("uid", "STRING"));
    assertTrue(thrown.getMessage().contains("Attempting to assign field"));
  }

  @Test
  public void testIndependence() {
    User newUser = new User("who", "am", "I");
    user.setValue("name", "BOGUS");
    assertNotEquals(newUser.getValue("name"), user.getValue("name"));
    assertNotEquals(newUser.getValue("email"), user.getValue("email"));
    assertNotEquals(newUser.getValue("password"), user.getValue("password"));
  }
}
