package Service;

import Entity.Entity;
import Entity.Field;

import java.util.List;

public class QueryFormatter {
  private static final String INSERT_TEMPLATE = "INSERT INTO %s %s VALUES %s";
  private static final String SELECT_BASE_TEMPLATE = "SELECT %s FROM %s WHERE %s";
  private static final String UPDATE_TEMPLATE = "UPDATE %s SET %s WHERE %s";
  private static final String DELETE_TEMPLATE = "DELETE FROM %s WHERE %s";

  public static <E extends Entity> String getInsertQuery(E entity) {
    List<Field> nonPrimaryFields = entity.getNonPrimaryFields();
    return String.format(
        INSERT_TEMPLATE,
        entity.getTableName(),
        composeSetFields(nonPrimaryFields),
        createBlankFields(nonPrimaryFields.size()));
  };


  public static String getSelectByFieldsQuery(Entity entity, List<Field> fields) {
    return String.format(
        SELECT_BASE_TEMPLATE,
        "*",
        entity.getTableName(),
        composeCompareFields(fields, JoinType.AND));
  }

  public static <E extends Entity> String getDeleteQuery(E entity) {
      return String.format(
              DELETE_TEMPLATE,
              entity.getTableName(),
              composeCompareFields(entity.getPrimaryKey(), JoinType.AND));
  }

  public static <E extends Entity> String getUpdateQuery(Entity entity) {
      return String.format(
              UPDATE_TEMPLATE,
              entity.getTableName(),
              composeCompareFields(entity.getNonPrimaryFields(), JoinType.COMMA),
              composeCompareFields(entity.getPrimaryKey(), JoinType.AND));
  }

  private static String createBlankFields(int n) {
    StringBuilder stringBuilder = new StringBuilder("(");
    for (int i = 0; i < n - 1; i++) {
      stringBuilder.append("?, ");
    }
    stringBuilder.append("?)");
    return stringBuilder.toString();
  }

  private static String composeSetFields(List<Field> fields) {
    return "(" + String.join(", ", Field.getFieldNames(fields)) + ")";
  }

  private static String composeCompareFields(List<Field> fields, JoinType jt) {
    return "(" + String.join("=?" + jt.content, Field.getFieldNames(fields)) + "=?)";
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
