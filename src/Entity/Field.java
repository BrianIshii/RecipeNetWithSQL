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
