package Entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Field<T> {
  public boolean isPrimaryKey;
  private Integer column;
  private String key;
  private T value;
  private Class<T> type;

  public Field(Class<T> type, String key, T value) {
    this.column = null;
    this.key = key;
    this.value = value;
    this.isPrimaryKey = false;
    this.type = type;
  }

  public Field(Class<T> type, String key, T value, boolean isPrimaryKey) {
    this.column = null;
    this.key = key;
    this.value = value;
    this.isPrimaryKey = isPrimaryKey;
    this.type = type;
  }

  public static List<String> getFieldNames(List<Field> fields) {
    List<String> fieldNames = new LinkedList<>();
    for (Field f : fields) {
      fieldNames.add(f.key);
    }
    return fieldNames;
  }

  public Integer getColumn() {
    return column;
  }

  public void setColumn(Integer column) {
    this.column = column;
  }

  public String getKey() {
    return key;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public Class getValueType() {
    return this.type;
  }

  public static boolean compareFields(List<Field> f1, List<Field> f2) {
    if (f1 == null || f2 == null || f1.size() != f2.size()) {
      return false;
    }

    for (int i = 0; i < f1.size(); i++) {
      if (!f1.get(i).equals(f2.get(i))) return false;
    }
    return true;
  }

  public static List<Field> applyTo(List<Field> from, List<Field> to, boolean isStrict) {
    for (Field f : from) {
      boolean added = false;
      for (Field g : to) {
        if (f.key.equals(g.key)) {
          g.setValue(f.getValue());
          added = true;
        }
      }
      if (!added && isStrict)
        throw new RuntimeException(
                String.format(
                        "Could not apply %s to %s, field does not exist", f.toString(), to.toString()));
    }
    return to;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Field{");
    sb.append("key='").append(key).append('\'');
    sb.append(", value=").append(value);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Field<?> field = (Field<?>) o;
    return isPrimaryKey == field.isPrimaryKey
        && Objects.equals(column, field.column)
        && Objects.equals(key, field.key)
        && Objects.equals(value, field.value);
  }
}
