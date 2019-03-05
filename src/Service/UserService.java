package Service;

import Entity.Field;
import Entity.Status;
import Entity.User;

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
    List<List<Field>> extractedFields =
        executorService.executeSelect(
            User.TABLE_NAME, user.getFields(), user.getField("email"), user.getField("password"));
    if (extractedFields.size() == 0) {
      System.out.println(
          String.format("Credentials username: %s, password: %s were not found", email, password));
      return null;
    }
    Field.applyTo(extractedFields.get(0), user.getFields(), true);

    user.setStatus(Status.SYNCED);
    return user;
  }
}
