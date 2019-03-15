package schema;

import java.util.Objects;

public class Field<T> {
    public boolean isPrimaryKey;
    private String key;
    private T value;
    private Class<T> type;

    public Field(Class<T> type, String key, T value) {
        this.key = key;
        this.value = value;
        this.isPrimaryKey = false;
        this.type = type;
    }

    public Field(Class<T> type, String key, T value, boolean isPrimaryKey) {
        this.key = key;
        this.value = value;
        this.isPrimaryKey = isPrimaryKey;
        this.type = type;
    }

    public Field(Class<T> type, String key, boolean isPrimaryKey) {
        this.key = key;
        this.value = null;
        this.isPrimaryKey = isPrimaryKey;
        this.type = type;
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

    public Field<T> clone() {
        return new Field<T>(this.type, this.key, this.value, this.isPrimaryKey);
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
                && Objects.equals(key, field.key)
                && Objects.equals(value, field.value);
    }
}
