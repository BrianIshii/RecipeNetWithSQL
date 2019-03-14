package exception;

import schema.Schema;

public class EntityNotFoundException extends ExecutorException {
  private static final String MSG = "Entity was expected, but not found";
  public EntityNotFoundException(Schema schema, String statement, Throwable cause) {
    super(MSG, schema, statement, cause);
  }
  public EntityNotFoundException() {
    super(MSG);
  }
}
