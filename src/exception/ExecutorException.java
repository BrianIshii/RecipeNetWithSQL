package exception;

import schema.Schema;

public class ExecutorException extends Exception {
  public ExecutorException(String message, Schema schema, String query, Throwable cause) {
    super(
        new StringBuilder()
            .append(message)
            .append(" : ")
            .append(schema.toString())
            .append("Using query: ")
            .append(query)
            .toString(),
        cause);
  }

  public ExecutorException(String message, Schema schema, String query) {
    super(
        new StringBuilder()
            .append(message)
            .append(" : ")
            .append(schema.toString())
            .append("Using query: ")
            .append(query)
            .toString());
  }

  public ExecutorException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExecutorException(String message, String query, Throwable cause) {
    super(String.format("%s: %s", message, query), cause);
  }

  public ExecutorException(Schema schema, String query, Throwable cause) {
    super(
        new StringBuilder(schema.toString())
            .append("Using query: ")
            .append(query)
            .append("\n")
            .toString(),
        cause);
  }

  public ExecutorException(String message) {
    super(message);
  }
}
