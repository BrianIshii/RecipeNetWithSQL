package service;

import schema.Field;
import formatter.QueryFormatter;
import schema.ResponseSchema;
import schema.Schema;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.*;
import java.util.*;

public class ExecutorService {
    private static Map<Class, Method> getters;
    private static Map<Class, Method> setters;

    private static ExecutorService instance = new ExecutorService();

    private static Connection con = DatabaseConnection.getInstance().getConnection();

    private ExecutorService() {
        getters = new HashMap<>();
        setters = new HashMap<>();
        try {
            // PS setters
            setters.put(
                    Long.class, PreparedStatement.class.getDeclaredMethod("setLong", int.class, long.class));
            setters.put(
                    String.class,
                    PreparedStatement.class.getDeclaredMethod("setString", int.class, String.class));
            setters.put(
                    Integer.class, PreparedStatement.class.getDeclaredMethod("setInt", int.class, int.class));
            setters.put(
                    Date.class, PreparedStatement.class.getDeclaredMethod("setDate", int.class, Date.class));

            // RS getters
            getters.put(Long.class, ResultSet.class.getDeclaredMethod("getLong", String.class));
            getters.put(String.class, ResultSet.class.getDeclaredMethod("getString", String.class));
            getters.put(Integer.class, ResultSet.class.getDeclaredMethod("getInt", String.class));
            getters.put(Date.class, ResultSet.class.getDeclaredMethod("getDate", String.class));

        } catch (NoSuchMethodException nsme) {
            throw new RuntimeException(nsme);
        }
    }

    public static ExecutorService getInstance() {
        return instance;
    }

    /**
     * Will extract the generated values from a PreparedStatement and mutate the given primary keys to
     * contain the new values.
     *
     * <p>Note: will currently only work with Longs.
     *
     * @param ps
     * @param primaryKeys
     * @return
     */
    private static boolean extractAndSetPK(PreparedStatement ps, List<Field> primaryKeys) {
        try {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                for (Field pk : primaryKeys) {
                    pk.setValue(generatedKeys.getLong(1));
                }
                return true;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }

    /**
     * Applies the given fields to a statement starting with the given index.
     *
     * @param ps
     * @param startIndex
     * @param fields
     */
    private static void applyFieldsToStatement(
            PreparedStatement ps, int startIndex, List<Field> fields) {
        try {
            int i = startIndex;
            for (Field f : fields) {
                applySet(ps, f.getValueType(), f.getValue(), i);
                i++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T applyGet(ResultSet rs, Class<T> valueType, String columnName) {
        try {
            Method method = getters.get(valueType);
            if (method == null)
                throw new RuntimeException(String.format("Class %s is not supported", valueType));
            return (T) method.invoke(rs, columnName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void applySet(PreparedStatement ps, Class<T> valueClass, T value, int index) {
        try {
            Method method = setters.get(valueClass);
            if (method == null)
                throw new RuntimeException(String.format("Class %s is not supported", valueClass));
            method.invoke(ps, index, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts fields into the databause under the table and populates fieldsToPopulate with the new
     * generated keys.
     *
     * @param tableName
     * @param fieldsToPopulate
     * @param fieldsToInsert
     * @return
     */
    public ResponseSchema executeInsert(
            String tableName, Schema fieldsToPopulate, Schema fieldsToInsert) {
        try {
            String query = QueryFormatter.getInsertQuery(tableName, fieldsToInsert.getFields());
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            applyFieldsToStatement(ps, 1, fieldsToInsert.getFields());
            System.out.println(ps.toString());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) return null;
            if (extractAndSetPK(ps, fieldsToPopulate.getFields())) {
                ps.close();
                return new ResponseSchema(fieldsToPopulate);
            }
        } catch (SQLException sqle) {
            if (sqle.getMessage().contains("Duplicate")) {
                System.err.println(sqle.getMessage());
                return null;
            } else {
                sqle.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Deletes a tuple with a matching fields.
     *
     * @param tableName
     * @param fieldsWhere
     * @return
     */
    public boolean executeDelete(String tableName, List<Field> fieldsWhere) {
        try {
            String query = QueryFormatter.getDeleteQuery(tableName, fieldsWhere);
            PreparedStatement ps = con.prepareStatement(query);
            applyFieldsToStatement(ps, 1, fieldsWhere);
            System.out.println(ps.toString());
            int affectedRows = ps.executeUpdate();
            ps.close();
            return affectedRows > 0;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    /**
     * Alternate version of delete for convenience.
     *
     * @param tableName
     * @param fieldsWhere
     * @return
     */
    public boolean executeDelete(String tableName, Field... fieldsWhere) {
        return executeDelete(tableName, Arrays.asList(fieldsWhere));
    }

    /**
     * Selects tuples based on the edit distance between a one of their fields and a string.
     *
     * @param tableName
     * @param fieldsToExpect
     * @param fieldNameToCompare
     * @param toValue
     * @param maxDistance
     * @return
     */
    public List<ResponseSchema> executeLevenshteinSelect(
            String tableName,
            Schema fieldsToExpect,
            String fieldNameToCompare,
            String toValue,
            int maxDistance) {
        String query =
                QueryFormatter.getLevensteinSelectQuery(
                        tableName, fieldsToExpect.getFields(), fieldNameToCompare, toValue, maxDistance);
        try {
            PreparedStatement ps = con.prepareStatement(query);
            return executeBaseSelect(ps, fieldsToExpect.getFields());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    private List<ResponseSchema> executeBaseSelect(PreparedStatement ps, List<Field> fieldsToExpect) {
        try {
            System.out.println(ps.toString());

            ResultSet rs = ps.executeQuery();
            // Populate a list of lists of fields with all the results
            List<ResponseSchema> allResults = new LinkedList<>();
            while (rs.next()) {
                ResponseSchema currentResult = new ResponseSchema();
                Field newField;
                for (Field f : fieldsToExpect) {
                    Object extractedValue = applyGet(rs, f.getValueType(), f.getKey());
                    currentResult.addField(f.getValueType(), f.getKey(), extractedValue, false);
                }
                allResults.add(currentResult);
            }
            ps.close();
            return allResults;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches an entity from the database using a search for the given fields.
     *
     * @param tableName
     * @param fieldsToExpect
     * @param fieldsWhere
     * @return
     */
    public List<ResponseSchema> executeSelect(
            String tableName, Schema fieldsToExpect, List<Field> fieldsWhere) {
        String query =
                QueryFormatter.getSelectQuery(tableName, fieldsToExpect.getFields(), fieldsWhere);
        try {
            PreparedStatement ps = con.prepareStatement(query);
            if (fieldsWhere != null) applyFieldsToStatement(ps, 1, fieldsWhere);
            return executeBaseSelect(ps, fieldsToExpect.getFields());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    public List<ResponseSchema> executeSelect(
            String tableName, Schema fieldsToExpect, Field... fieldsWhere) {
        return executeSelect(tableName, fieldsToExpect, Arrays.asList(fieldsWhere));
    }

    public List<ResponseSchema> executeSelect(String tableName, Schema fieldsToExpect) {
        return executeSelect(tableName, fieldsToExpect, new ArrayList<>());
    }

    public boolean executeUpdate(
            String tableName, Schema fieldsToSet, Schema fieldsWhere) {
        try {
            String query =
                    QueryFormatter.getUpdateQuery(
                            tableName, fieldsToSet.getFields(), fieldsWhere.getFields());
            PreparedStatement ps = con.prepareStatement(query);
            applyFieldsToStatement(ps, 1, fieldsToSet.getFields());
            applyFieldsToStatement(ps, 1 + fieldsToSet.numFields(), fieldsWhere.getFields());
            System.out.println(ps.toString());

            int affectedRows = ps.executeUpdate();
            ps.close();
            return affectedRows > 0;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
