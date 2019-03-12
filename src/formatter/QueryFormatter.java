package formatter;

import java.util.List;
import java.util.stream.Collectors;

public class QueryFormatter {
  private static final String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES %s;";
  private static final String SELECT_BASE_TEMPLATE = "SELECT %s FROM %s;";
  private static final String SELECT_WHERE_TEMPLATE = "SELECT %s FROM %s WHERE %s;";
    private static final String LEVENSTEIN_SELECT_TEMPLATE = "SELECT %s FROM %s WHERE levenshtein(%s, '?') < %s";
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

    public static String getLevensteinSelectQuery(String tableName, List<Field> fieldsToExpect, String fieldToCompare, String toValue, int maxDistance) {
        return String.format(LEVENSTEIN_SELECT_TEMPLATE, composeFields(fieldsToExpect), tableName, fieldToCompare, toValue, maxDistance);
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
      List<String> fieldNames = fields.stream().map(f -> f.getKey()).collect(Collectors.toList());
      return String.join(", ", fieldNames);
  }

  private static String composeCompareFields(List<Field> fields, JoinType jt) {
      StringBuilder sb = new StringBuilder();
      List<String> fieldNames = fields.stream().map(f -> f.getKey()).collect(Collectors.toList());
      return String.join("=?" + jt.getContent(), fieldNames) + "=?";
  }
}
