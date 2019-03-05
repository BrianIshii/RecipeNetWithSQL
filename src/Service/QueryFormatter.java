package Service;

import Entity.Field;

import java.util.List;

public class QueryFormatter {
  private static final String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES %s;";
  private static final String SELECT_BASE_TEMPLATE = "SELECT %s FROM %s;";
  private static final String SELECT_WHERE_TEMPLATE = "SELECT %s FROM %s WHERE %s;";
  private static final String UPDATE_TEMPLATE = "UPDATE %s SET %s WHERE %s;";
  private static final String DELETE_TEMPLATE = "DELETE FROM %s WHERE %s;";

  public static String getInsertQuery(String tableName, List<Field> newFields) {
    return String.format(
        INSERT_TEMPLATE, tableName, composeFields(newFields), createBlankFields(newFields.size()));
  }

  public static String getSelectQuery(
      String tableName, List<Field> fieldsToExpect, List<Field> fieldsWhere) {
    if (fieldsWhere == null || fieldsWhere.size() == 0) {
      return String.format(SELECT_BASE_TEMPLATE, composeFields(fieldsToExpect), tableName);
    } else {
      return String.format(
          SELECT_WHERE_TEMPLATE,
          composeFields(fieldsToExpect),
          tableName,
          composeCompareFields(fieldsWhere, JoinType.AND));
    }
  }

  public static String getDeleteQuery(String tableName, List<Field> fieldsWhere) {
    return String.format(
        DELETE_TEMPLATE, tableName, composeCompareFields(fieldsWhere, JoinType.AND));
  }

  public static String getUpdateQuery(
      String tableName, List<Field> fieldsSet, List<Field> fieldsWhere) {
    return String.format(
        UPDATE_TEMPLATE,
        tableName,
        composeCompareFields(fieldsSet, JoinType.COMMA),
        composeCompareFields(fieldsWhere, JoinType.AND));
  }

  private static String createBlankFields(int n) {
    StringBuilder stringBuilder = new StringBuilder("(");
    for (int i = 0; i < n - 1; i++) {
      stringBuilder.append("?, ");
    }
    stringBuilder.append("?)");
    return stringBuilder.toString();
  }

  private static String composeFields(List<Field> fields) {
    return String.join(", ", Field.getFieldNames(fields));
  }

  private static String composeCompareFields(List<Field> fields, JoinType jt) {
    return String.join("=?" + jt.content, Field.getFieldNames(fields)) + "=?";
  }

  private enum JoinType {
    COMMA(", "),
    AND(" AND "),
    OR(" OR ");

    private final String content;

    JoinType(String s) {
      this.content = s;
    }
  }
}
