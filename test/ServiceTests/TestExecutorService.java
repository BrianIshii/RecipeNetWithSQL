package ServiceTests;

import static EntityTests.Constants.EMAIL;
import static EntityTests.Constants.NAME;
import static EntityTests.Constants.PASSWORD;

import Entity.Status;
import Entity.User;
import Service.ExecutorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

public class TestExecutorService {
    private static ExecutorService executorService;

    @BeforeAll
    public static void beforeAll() {
        executorService = ExecutorService.getInstance();
    }

    @Test
    public void test1InsertUser() {
        User user = new User(NAME, EMAIL, PASSWORD);
        user = executorService.executeInsert(user);
        assertNotNull(user);
        assertEquals(NAME, user.getValue("name"));
        assertEquals(EMAIL, user.getValue("email"));
        assertEquals(PASSWORD, user.getValue("password"));
        assertNotEquals(Status.NEW, user.getStatus());
    }

    @Test
    public void test2GetByPrimaryKey() {
        User user = new User(null, EMAIL, PASSWORD);
        user = executorService.executeFetchByBodyMatch(user);
        assertNotNull(user);
        assertEquals(Status.UP_TO_DATE, user.getStatus());
        assertEquals(NAME, user.getValue("name"));
        assertEquals(EMAIL, user.getValue("email"));
        assertEquals(PASSWORD, user.getValue("password"));
    }

    @Test
    public void test3DeleteUser() {
        User user = new User(null, EMAIL, PASSWORD);
        user = executorService.executeFetchByBodyMatch(user);
        assertEquals(Status.UP_TO_DATE, user.getStatus());
        user = executorService.executeDelete(user);
        assertEquals(Status.DELETED, user.getStatus());
    }
}
