package RecipeNet.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Schema {
    protected List<Field> fields;

    public Schema() {
        this.fields = new ArrayList<>();
    }

    public <S extends Schema> Schema(S sourceSchema) {
        fields = deepCopyFields(sourceSchema);
    }

    public static <S extends Schema> List<Field> deepCopyFields(S sourceSchema) {
        List<Field> newFields = new ArrayList<>();
        Iterator<Field> iterator = sourceSchema.fields.iterator();
        while (iterator.hasNext()) newFields.add(iterator.next().clone());
        return newFields;
    }

    public <S extends Schema, T extends Object> S addField(
            Class<T> type, String key, T value, boolean isPrimary) {
        this.fields.add(new Field<T>(type, key, value, isPrimary));
        return (S) this;
    }

    public <S extends Schema, T extends Object> S addField(
            Class<T> type, String key, boolean isPrimary) {
        this.fields.add(new Field<T>(type, key, isPrimary));
        return (S) this;
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public Field getField(String key) {
        return fields.stream()
                .filter(f -> f.getKey().equals(key))
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("Key %s does not exist", key)));
    }

    public boolean containsKey(String key) {
        return fields.stream().anyMatch(f -> f.getKey().equals(key));
    }

    public Object getFieldValue(String key) {
        return getField(key).getValue();
    }

    public void setFieldValue(String key, Object value) {
        Field f = getField(key);
        //Assigning null is allowed, but anything else must have the right type
        if (value == null || value.getClass() == f.getValueType()) {
            f.setValue(value);
        } else {
            throw new RuntimeException(
                    String.format(
                            "Attempting to assign field to value of type %s. %s is of type %s",
                            value.getClass(), key, f.getValueType()));
        }
    }

    public List<Field> getPrimaryFields() {
        List<Field> pks =
                fields.stream()
                        .filter(f -> f.isPrimaryKey)
                        .collect(Collectors.toCollection(ArrayList::new));
        return pks;
    }

    public List<Field> getNonPrimaryFields() {
        List<Field> nonPK =
                fields.stream()
                        .filter(f -> !f.isPrimaryKey)
                        .collect(Collectors.toCollection(ArrayList::new));
        return nonPK;
    }

    public Integer numFields() {
        return fields.size();
    }

    /**
     * Attempts to set matching fields in the other Schema to the corresponding values in this schema.
     * If isStrict is enabled, missing keys in the other Schema will cause this method to throw
     * errors.
     *
     * @param other
     * @param isStrict
     * @param <S>
     * @return
     */
    public <S extends Schema> void applyValuesTo(S other, boolean isStrict) {
        fields.stream()
                .forEach(
                        f -> {
                            try {
                                other.setFieldValue(f.getKey(), f.getValue());
                            } catch (RuntimeException re) {
                                if (isStrict) throw re;
                            }
                        });
    }

    public static <S extends Schema> boolean keysMatch(S s1, S s2) {
        if (s1 == null || s2 == null) return false;
        try {
            return s1.fields.stream().allMatch(f -> s2.containsKey(f.getKey()));
        } catch (RuntimeException re) {
            return false;
        }
    }

    public List<String> getFieldNames() {
        return fields.stream().map(f -> f.getKey()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Schema{");
        sb.append("fields=").append(fields);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schema schema = (Schema) o;
        return fields.equals(schema.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }
}
