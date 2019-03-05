package Entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Entity {
  protected Status status = Status.NEW;
  protected List<Field> fields = new LinkedList<Field>();

  public abstract String getTableName();

  public Object getValue(String key) {
    for (Field f : fields) {
      if (f.getKey().equals(key)) {
        return f.getValue();
      }
    }
    throw new RuntimeException(String.format("Key %s does not exist on %s", key, this.getClass()));
  }

  public void setValue(String key, Object value) {
    Field field = getField(key);
    if (field.getValue().getClass() == value.getClass()) {
      field.setValue(value);
      status = Status.DIRTY;
      return;
    }
    throw new RuntimeException(
        String.format(
            "Attempting to assign field %s of type %s to value %s",
            key, field.getValue().getClass(), value));
  }

  public List<Field> getFields() {
    return this.fields;
  }

  public Field getField(String key) {
    for (Field f : fields) {
      if (f.getKey().equals(key)) {
        return f;
      }
    }
    throw new RuntimeException(String.format("Field was not found using key: %s", key));
  }

  public void addField(Field field) {
    field.setColumn(fields.size() + 1);
    fields.add(field);
  }

  public void addField(Class type, String key, Object value, boolean isPrimaryKey) {
    if (value == null || type == value.getClass()) {
      Field f = new Field(type, key, value, isPrimaryKey);
      addField(f);
      return;
    }
    throw new RuntimeException(
        String.format(
            "Attempting to create field %s of type %s to value %s", key, type, value.getClass()));
  }

  public List<Field> getPrimaryKey() {
    List<Field> returnList = new ArrayList<Field>();
    for (Field f : fields) {
      if (f.isPrimaryKey) {
        returnList.add(f);
      }
    }
    if (returnList.size() == 0) throw new RuntimeException("Primary key was not defined");

    return returnList;
  }

  public List<Field> getNonPrimaryFields() {
    List<Field> returnList = new ArrayList<Field>();
    for (Field a : fields) {
      if (!a.isPrimaryKey) {
        returnList.add(a);
      }
    }
    return returnList;
  }

  public Status getStatus() {
    return this.status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Field.compareFields(fields, entity.fields);
    }
}
