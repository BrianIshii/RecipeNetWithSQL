package Entity;

import java.util.Arrays;
import java.util.List;

public class User implements Entity {
  public static final List<String> attributes = Arrays.asList("name", "email", "password");
  public static final String PKName = "rid";
  private Long uid;
  private String name;
  private String email;
  private String pass;

  public User(Long uid, String name, String email, String pass) {
    this.uid = uid;
    this.name = name;
    this.email = email;
    this.pass = pass;
  }

  public User(String name, String email, String pass) {
    this.uid = 0L;
    this.name = name;
    this.email = email;
    this.pass = pass;
  }

  public User(String email, String pass) {
    this.uid = 0L;
    this.name = null;
    this.email = email;
    this.pass = pass;
  }

  public Long getUid() {
    return uid;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPass() {
    return pass;
  }

  public void setUid(Long uid) {
    this.uid = uid;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("User{");
    sb.append("uid=").append(uid);
    sb.append(", name='").append(name).append('\'');
    sb.append(", email='").append(email).append('\'');
    sb.append(", pass='").append(pass).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
