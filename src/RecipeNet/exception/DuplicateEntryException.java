package RecipeNet.exception;

import RecipeNet.schema.Schema;

import java.sql.PreparedStatement;

public class DuplicateEntryException extends ExecutorException {
  public DuplicateEntryException(Schema schema, PreparedStatement preparedStatement, Throwable cause) {
    super("Attempted to insert duplicate entry into database", schema, preparedStatement, cause);
  }
}
