package service;

import entity.User;
import exception.EntityNotFoundException;
import exception.ExecutorException;
import schema.ResponseSchema;

import java.util.ArrayList;
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
  public User authenticate(String email, String password) throws ExecutorException {
    User user = new User(email, password);
    List<ResponseSchema> response =
        executorService.executeSelect(
            User.TABLE_NAME,
            User.ENTITY_FIELDS,
            user.getField(User.EMAIL),
            user.getField(User.PASSWORD));
    if (response.size() == 0) throw new EntityNotFoundException();
    response.get(0).applyValuesTo(user, true);
    user.setSynced();
    return user;
  }

  public List<User> fuzzyNameSearch(String name, int maxEditDistance) throws ExecutorException {
    List<ResponseSchema> response =
        executorService.executeLevenshteinSelect(
            User.TABLE_NAME, User.ENTITY_FIELDS, User.NAME, name, maxEditDistance);
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
