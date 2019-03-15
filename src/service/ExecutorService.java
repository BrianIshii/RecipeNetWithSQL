package service;

import exception.DuplicateEntryException;
import exception.ExecutorException;
import exception.NoRowsAffectedException;
import exception.PreparedStatementCloseFailureException;
import formatter.QueryFormatter;
import schema.Field;
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
  private static void extractAndSetPK(PreparedStatement ps, List<Field> primaryKeys)
      throws ExecutorException {
    try {
      ResultSet generatedKeys = ps.getGeneratedKeys();
      if (generatedKeys.next()) {
        for (Field pk : primaryKeys) {
          pk.setValue(generatedKeys.getLong(1));
        }
      }
    } catch (SQLException sqle) {
      throw new ExecutorException(
          "Unable to extract generated keys from prepared statement.", sqle);
    }
  }

  /**
   * Applies the given fields to a statement starting with the given index.
   *
   * @param ps
   * @param startIndex
   * @param fields
   */
  private static void applyFieldsToStatement(
      PreparedStatement ps, int startIndex, List<Field> fields) throws ExecutorException {
    int i = startIndex;
    for (Field f : fields) {
      applySet(ps, f.getValueType(), f.getValue(), i);
      i++;
    }
  }

  private static <T> T applyGet(ResultSet rs, Class<T> valueType, String columnName)
      throws ExecutorException {
    try {
      Method method = getters.get(valueType);
      if (method == null)
        throw new RuntimeException(String.format("Class %s is not supported", valueType));
      return (T) method.invoke(rs, columnName);
    } catch (Exception e) {
      throw new ExecutorException("Issue occurred while extracting fields from ResultSet.", e);
    }
  }

  private static <T> void applySet(PreparedStatement ps, Class<T> valueClass, T value, int index)
      throws ExecutorException {
    try {
      Method method = setters.get(valueClass);
      if (method == null)
        throw new RuntimeException(String.format("Class %s is not supported", valueClass));
      method.invoke(ps, index, value);
    } catch (Exception e) {
      throw new ExecutorException("Issue occurred while setting values of a PreparedStatement", e);
    }
  }

  /**
   * Inserts fields into the database under the table and populates fieldsToPopulate with the new
   * generated keys.
   *
   * @param tableName
   * @param fieldsToPopulate
   * @param fieldsToInsert
   * @return
   */
  public ResponseSchema executeInsert(
      String tableName, Schema fieldsToPopulate, Schema fieldsToInsert) throws ExecutorException {
    String query = QueryFormatter.getInsertQuery(tableName, fieldsToInsert.getFields());
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      applyFieldsToStatement(ps, 1, fieldsToInsert.getFields());
      System.out.println(ps.toString());
      int affectedRows = ps.executeUpdate();
      if (affectedRows == 0) throw new NoRowsAffectedException(fieldsToInsert, ps);
      extractAndSetPK(ps, fieldsToPopulate.getFields());

      return new ResponseSchema(fieldsToPopulate);
    } catch (SQLException sqle) {
      if (sqle.getMessage().contains("Duplicate")) {
        throw new DuplicateEntryException(fieldsToInsert, ps, sqle);
      } else {
        throw new ExecutorException(fieldsToInsert, ps, sqle);
      }
    } finally {
      safeCloseConnection(ps);
    }
  }

  /**
   * Deletes a tuple with a matching fields.
   *
   * @param tableName
   * @param fieldsWhere
   * @return
   */
  public void executeDelete(String tableName, List<Field> fieldsWhere) throws ExecutorException {
    String query = QueryFormatter.getDeleteQuery(tableName, fieldsWhere);
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query);
      applyFieldsToStatement(ps, 1, fieldsWhere);
      System.out.println(ps.toString());
      int affectedRows = ps.executeUpdate();
      if (affectedRows == 0) throw new NoRowsAffectedException(ps);
    } catch (SQLException sqle) {
      throw new ExecutorException(
          "Issue occurred while attempting to execute DELETE.", ps, sqle);
    } finally {
      safeCloseConnection(ps);
    }
  }

  /**
   * Alternate version of delete for convenience.
   *
   * @param tableName
   * @param fieldsWhere
   * @return
   */
  public void executeDelete(String tableName, Field... fieldsWhere) throws ExecutorException {
    executeDelete(tableName, Arrays.asList(fieldsWhere));
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
      int maxDistance)
      throws ExecutorException {
    String query =
        QueryFormatter.getLevensteinSelectQuery(
            tableName, fieldsToExpect.getFields(), fieldNameToCompare, toValue, maxDistance);
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query);
      return executeBaseSelect(ps, fieldsToExpect.getFields());
    } catch (SQLException sqle) {
      throw new ExecutorException("Issue occurred while preparing statement", sqle);
    }
  }

  private List<ResponseSchema> executeBaseSelect(PreparedStatement ps, List<Field> fieldsToExpect)
      throws ExecutorException {
    List<ResponseSchema> allResults = new LinkedList<>();
    try {
      System.out.println(ps.toString());
      ResultSet rs = ps.executeQuery();
      // Populate a list of lists of fields with all the results
      while (rs.next()) {
        ResponseSchema currentResult = new ResponseSchema();
        Field newField;
        for (Field f : fieldsToExpect) {
          Object extractedValue = applyGet(rs, f.getValueType(), f.getKey());
          currentResult.addField(f.getValueType(), f.getKey(), extractedValue, false);
        }
        allResults.add(currentResult);
      }
    } catch (SQLException sqle) {
      throw new ExecutorException("Issue occurred while executing select. ", ps, sqle);
    } catch (Exception e) {
      throw new ExecutorException("Issue occurred while preparing statement", e);
    } finally {
      safeCloseConnection(ps);
    }
    return allResults; //Must be out here, otherwise Exceptions are suppressed
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
      String tableName, Schema fieldsToExpect, List<Field> fieldsWhere) throws ExecutorException {
    String query =
        QueryFormatter.getSelectQuery(tableName, fieldsToExpect.getFields(), fieldsWhere);
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query);
      if (fieldsWhere != null) applyFieldsToStatement(ps, 1, fieldsWhere);
      return executeBaseSelect(ps, fieldsToExpect.getFields());
    } catch (SQLException sqle) {
      throw new ExecutorException(
          "Issue occurred while attempting to prepare statement", ps, sqle);
    }
  }

  public List<ResponseSchema> executeSelect(
      String tableName, Schema fieldsToExpect, Field... fieldsWhere) throws ExecutorException {
    return executeSelect(tableName, fieldsToExpect, Arrays.asList(fieldsWhere));
  }

  public List<ResponseSchema> executeSelect(String tableName, Schema fieldsToExpect)
      throws ExecutorException {
    return executeSelect(tableName, fieldsToExpect, new ArrayList<>());
  }

  public void executeUpdate(String tableName, Schema fieldsToSet, Schema fieldsWhere)
      throws ExecutorException {
    String query =
        QueryFormatter.getUpdateQuery(tableName, fieldsToSet.getFields(), fieldsWhere.getFields());
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query);
      applyFieldsToStatement(ps, 1, fieldsToSet.getFields());
      applyFieldsToStatement(ps, 1 + fieldsToSet.numFields(), fieldsWhere.getFields());
      System.out.println(ps.toString());

      int affectedRows = ps.executeUpdate();
      if (affectedRows == 0) throw new NoRowsAffectedException(ps);
    } catch (SQLException sqle) {
      throw new ExecutorException(
          "Issue occurred while attempting to execute update.", ps, sqle);
    } catch (Exception e) {
      throw new ExecutorException("Issue occurred while preparing statement.", ps, e);
    } finally {
      safeCloseConnection(ps);
    }
  }

  private void safeCloseConnection(PreparedStatement preparedStatement) throws ExecutorException {
    if (preparedStatement != null) {
      try {
        preparedStatement.close();
      } catch (SQLException sqle) {
        throw new PreparedStatementCloseFailureException(sqle);
      }
    }
  }
}
