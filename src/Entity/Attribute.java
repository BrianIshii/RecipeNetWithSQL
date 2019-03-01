package Entity;

import java.lang.reflect.Field;

public class Attribute {
  public String key;
  public Object value;

  public Attribute(String key, Object value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    if(value.getClass() == String.class) {
      return "'" + value + "'";
    }
    return value.toString();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Attribute{");
    sb.append("key='").append(key).append('\'');
    sb.append(", value=").append(value);
    sb.append('}');
    return sb.toString();
  }
}
