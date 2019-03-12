package foo;

import schema.Schema;

public abstract class Entity extends Schema {
  protected Status status = Status.NEW;

  public abstract String getTableName();

  public Status getStatus() {
    return this.status;
  }

  public void setSynced() {
    this.status = Status.SYNCED;
  }

  public void setDeleted() {
    this.status = Status.DELETED_LOCALLY;
  }

  public void setNew() {
    this.status = Status.NEW;
  }

  public void setDirty() {
    this.status = Status.DIRTY;
  }

  public void setFieldValue(String key, Object value) {
    setDirty();
    super.setFieldValue(key, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Entity entity = (Entity) o;
    return this.getTableName().equals(entity.getTableName()) && Schema.keysMatch(this, entity);
  }

  public String toString() {
    return fields.toString();
  }
}
