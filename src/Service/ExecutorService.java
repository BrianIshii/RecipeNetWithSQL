package Service;

import Entity.Entity;
import Entity.Field;
import Entity.Status;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class ExecutorService {
  // PS setters
  private static Method setLong;
  private static Method setString;
  private static Method setInt;
  private static Method setDate;

  // RS getters
  private static Method getLong;
  private static Method getString;
  private static Method getInt;
  private static Method getDate;

  // Execute
  private static Method executeUpdate;
  private static Method executeQuery;

  private static List<Class> methodTypes;
  private static List<Method> getters;
  private static List<Method> setters;

  private static ExecutorService instance = new ExecutorService();

  private static Connection con = DatabaseConnection.getInstance().getConnection();

  private ExecutorService() {
    try {
      // PS setters
      setLong = PreparedStatement.class.getDeclaredMethod("setLong", int.class, long.class);
      setString = PreparedStatement.class.getDeclaredMethod("setString", int.class, String.class);
      setInt = PreparedStatement.class.getDeclaredMethod("setInt", int.class, int.class);
      setDate = PreparedStatement.class.getDeclaredMethod("setDate", int.class, Date.class);

      // RS getters
      getLong = ResultSet.class.getDeclaredMethod("getLong", String.class);
      getString = ResultSet.class.getDeclaredMethod("getString", String.class);
      getInt = ResultSet.class.getDeclaredMethod("getInt", String.class);
      getDate = ResultSet.class.getDeclaredMethod("getDate", String.class);

      executeUpdate = PreparedStatement.class.getDeclaredMethod("executeUpdate");
      executeQuery = PreparedStatement.class.getDeclaredMethod("executeQuery");

    } catch (NoSuchMethodException nsme) {
      throw new RuntimeException(nsme);
    }
    methodTypes = Arrays.asList(Long.class, String.class, Date.class, Integer.class);
    getters = Arrays.asList(getLong, getString, getDate, getInt);
    setters = Arrays.asList(setLong, setString, setDate, setInt);
  }

  public static ExecutorService getInstance() {
    return instance;
  }

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

  private static Object applyGet(ResultSet rs, Class c, String columnName) {
    try {
      for (int i = 0; i < methodTypes.size(); i++) {
        Class m = methodTypes.get(i);
        if (c.equals(m)) {
          return getters.get(i).invoke(rs, columnName);
        }
      }
      throw new IllegalArgumentException(String.format("Class of type %s is not supported", c));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void applySet(PreparedStatement ps, Class c, Object value, int index) {
    try {
      for (int i = 0; i < methodTypes.size(); i++) {
        Class m = methodTypes.get(i);
        if (c.equals(m)) {
          setters.get(i).invoke(ps, index, value);
          return;
        }
      }
      throw new IllegalArgumentException(String.format("Class of type %s is not supported", c));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public <E extends Entity> E executeInsert(E entity) {
    String baseQuery = QueryFormatter.getInsertQuery(entity);
    try {
      PreparedStatement ps = con.prepareStatement(baseQuery, Statement.RETURN_GENERATED_KEYS);
      applyFieldsToStatement(ps, 1, entity.getNonPrimaryFields());
      System.out.println(ps.toString());
      int affectedRows = ps.executeUpdate();
      if (affectedRows == 0) entity.setStatus(Status.INVALID);
      else
        entity.setStatus(
            extractAndSetPK(ps, entity.getPrimaryKey()) ? Status.UP_TO_DATE : Status.INVALID);
      ps.close();
    } catch (SQLException sqle) {
      if (sqle.getMessage().contains("Duplicate")) {
        return executeFetchByBodyMatch(entity);
      } else {
        sqle.printStackTrace();
        entity.setStatus(Status.INVALID);
      }
    }
    return entity;
  }

  public <E extends Entity> E executeDelete(E entity) {
    String baseQuery = QueryFormatter.getDeleteQuery(entity);
    try {
      PreparedStatement ps = con.prepareStatement(baseQuery);
      applyFieldsToStatement(ps, 1, entity.getPrimaryKey());
      System.out.println(ps.toString());
      entity.setStatus(ps.executeUpdate() > 0 ? Status.DELETED : Status.DIRTY);
      ps.close();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      entity.setStatus(Status.DIRTY);
    }
    return entity;
  }

  public <E extends Entity> E executeFetchByPK(E entity) {
    List<Field> fieldsToCompare = entity.getPrimaryKey();
    String baseQuery = QueryFormatter.getSelectByFieldsQuery(entity, fieldsToCompare);
    return executeAndPopulate(entity, baseQuery, fieldsToCompare);
  }

  public <E extends Entity> E executeFetchByBodyMatch(E entity) {
    List<Field> fieldsToCompare = entity.getNonPrimaryFields();
    fieldsToCompare.removeIf(f -> f.getValue() == null); // Will not compare field if it is null
    String baseQuery = QueryFormatter.getSelectByFieldsQuery(entity, fieldsToCompare);
    return executeAndPopulate(entity, baseQuery, fieldsToCompare);
  }

  public <E extends Entity> E executeFetch(E entity, List<Field> fields) {
    String baseQuery = QueryFormatter.getSelectByFieldsQuery(entity, fields);
    return executeAndPopulate(entity, baseQuery, fields);
  }

  private <E extends Entity> E executeAndPopulate(
      E entity, String query, List<Field> fieldsToCompare) {
    try {
      PreparedStatement ps = con.prepareStatement(query);
      applyFieldsToStatement(ps, 1, fieldsToCompare);
      System.out.println(ps.toString());
      ResultSet resultSet = (ResultSet) executeQuery.invoke(ps);

      // Populate the entity with the new data
      if (resultSet.next()) {
        for (Field f : entity.getFields()) {
          f.setValue(applyGet(resultSet, f.getValueType(), f.getKey()));
        }
        entity.setStatus(Status.UP_TO_DATE);
      } else {
        entity.setStatus(Status.INVALID);
      }
      ps.close();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      entity.setStatus(Status.INVALID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return entity;
  }

  public <E extends Entity> E executeUpdate(E entity) {
    String baseQuery = QueryFormatter.getUpdateQuery(entity);
    List<Field> fields = entity.getNonPrimaryFields();
    fields.addAll(entity.getPrimaryKey());
    try {
      PreparedStatement ps = con.prepareStatement(baseQuery);
      applyFieldsToStatement(ps, 1, fields);
      System.out.println(ps.toString());
      Integer rowsAffected = (Integer) executeUpdate.invoke(ps);
      entity.setStatus(rowsAffected > 0 ? Status.UP_TO_DATE : Status.INVALID);
      ps.close();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      entity.setStatus(Status.INVALID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return entity;
  }
}
