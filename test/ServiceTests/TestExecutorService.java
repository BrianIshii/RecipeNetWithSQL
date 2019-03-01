package ServiceTests;

import Entity.Status;
import Entity.User;
import Service.ExecutorService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static EntityTests.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestExecutorService {
  private static ExecutorService executorService;

  @BeforeAll
  public static void beforeAll() {
    executorService = ExecutorService.getInstance();
    User user = new User(NAME, EMAIL, PASSWORD);
    executorService.executeInsert(user);
    assertNotNull(user);
    assertEquals(NAME, user.getValue("name"));
    assertEquals(EMAIL, user.getValue("email"));
    assertEquals(PASSWORD, user.getValue("password"));
    assertNotEquals(Status.NEW, user.getStatus());
  }

  @AfterAll
  public static void afterAll() {
    User user = new User(null, EMAIL, PASSWORD);
    executorService.executeFetchByBodyMatch(user);
    assertEquals(Status.UP_TO_DATE, user.getStatus());
    executorService.executeDelete(user);
    assertEquals(Status.DELETED, user.getStatus());
  }

  @Test
  public void testGetByBodyMatch() {
    User user = new User(null, EMAIL, PASSWORD);
    executorService.executeFetchByBodyMatch(user);
    assertNotNull(user);
    assertEquals(Status.UP_TO_DATE, user.getStatus());
    assertEquals(NAME, user.getValue("name"));
    assertEquals(EMAIL, user.getValue("email"));
    assertEquals(PASSWORD, user.getValue("password"));
  }

  @Test
  public void testUpdate() {
    User user = new User("User", "s@g", "test");
    executorService.executeInsert(user);
    if (user.getStatus() == Status.INVALID) {
      user = executorService.executeFetch(user, Arrays.asList(user.getField("email")));
      user = executorService.executeDelete(user);
      assertEquals(Status.DELETED, user.getStatus(), "Deleting left over user");
    }
    user = new User("User", "s@g", "test");
    executorService.executeInsert(user);
    assertEquals(Status.UP_TO_DATE, user.getStatus());
    user.setValue("name", "Other");
    assertEquals(Status.DIRTY, user.getStatus());
    executorService.executeUpdate(user);
    assertEquals(Status.UP_TO_DATE, user.getStatus());
    assertEquals("Other", user.getValue("name"));
    executorService.executeDelete(user);
  }
}
