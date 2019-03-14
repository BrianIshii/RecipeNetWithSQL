package exception;

import schema.Schema;

public class EntityNotFoundException extends ExecutorException {
  public EntityNotFoundException(Schema schema, String statement, Throwable cause) {
    super("Entity was expected, but not found", schema, statement, cause);
  }
}
