package service;

import entity.User;
import schema.ResponseSchema;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserService extends EntityService {
  private static UserService ourInstance = new UserService();

  public static UserService getInstance() {
    return ourInstance;
  }

  private UserService() {}

  /**
   * Returns a fully populated User.
   *
   * @param email
   * @param password
   * @return
   */
  public User authenticate(String email, String password) {
    User user = new User(email, password);
    List<ResponseSchema> response =
        executorService.executeSelect(
            User.TABLE_NAME, User.ENTITY_FIELDS, user.getField("email"), user.getField("password"));
    if (response.size() == 0) {
      System.out.println(
          String.format("Credentials username: %s, password: %s were not found", email, password));
      return null;
    }
    response.get(0).applyValuesTo(user, true);
    user.setSynced();
    return user;
  }

  public List<User> fuzzyNameSearch(String name, int maxEditDistance) {
    List<ResponseSchema> response =
        executorService.executeLevenshteinSelect(
            User.TABLE_NAME, User.ENTITY_FIELDS, "name", name, maxEditDistance);
    if (response == null) {
      return new LinkedList<>();
    }

    List<User> users = new ArrayList<>();
    User user;
    for (ResponseSchema res : response) {
      user = new User();
      res.applyValuesTo(user, true);
      user.setSynced();
      users.add(user);
    }
    return users;
  }
}
