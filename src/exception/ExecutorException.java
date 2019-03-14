package exception;

import schema.Schema;

public class ExecutorException extends Exception {
  public ExecutorException(String message, Schema schema, String statement, Throwable cause) {
    super(
        new StringBuilder()
            .append(statement)
            .append(" : ")
            .append(schema.toString())
            .append("Using statement: ")
            .append(statement)
            .toString(),
        cause);
  }

  public ExecutorException(String message, Schema schema, String statement) {
    super(
        new StringBuilder()
            .append(statement)
            .append(" : ")
            .append(schema.toString())
            .append("Using statement: ")
            .append(statement)
            .toString());
  }

  public ExecutorException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExecutorException(Schema schema, String statement, Throwable cause) {
    super(
        new StringBuilder(schema.toString())
            .append("Using statement: ")
            .append(statement)
            .append("\n")
            .toString(),
        cause);
  }
}
