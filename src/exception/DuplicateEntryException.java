package exception;

import schema.Schema;

public class DuplicateEntryException extends ExecutorException {
  public DuplicateEntryException(Schema schema, String statement, Throwable cause) {
    super("Attempted to insert duplicate entry into database", schema, statement, cause);
  }
}
