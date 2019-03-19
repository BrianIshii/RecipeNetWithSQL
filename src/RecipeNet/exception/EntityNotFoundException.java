package RecipeNet.exception;

import RecipeNet.schema.Schema;

import java.sql.PreparedStatement;

public class EntityNotFoundException extends ExecutorException {
  private static final String MSG = "Entity was expected, but not found";
  public EntityNotFoundException(Schema schema, PreparedStatement preparedStatement, Throwable cause) {
    super(MSG, schema, preparedStatement, cause);
  }
  public EntityNotFoundException() {
    super(MSG);
  }
}
