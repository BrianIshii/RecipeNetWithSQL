package exception;

import schema.Schema;

import java.sql.PreparedStatement;

public class ExecutorException extends Exception {
  public ExecutorException(String message, Schema schema, PreparedStatement preparedStatement, Throwable cause) {
    super(
        new StringBuilder()
            .append(message)
            .append(" : ")
            .append(schema.toString())
            .append("Using query: ")
            .append(preparedStatement.toString())
            .toString(),
        cause);
  }

  public ExecutorException(String message, Schema schema, PreparedStatement preparedStatement) {
    super(
        new StringBuilder()
            .append(message)
            .append(" : ")
            .append(schema.toString())
            .append("Using query: ")
            .append(preparedStatement.toString())
            .toString());
  }

  public ExecutorException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExecutorException(String message, PreparedStatement preparedStatement, Throwable cause) {
    super(String.format("%s: %s", message, preparedStatement.toString()), cause);
  }

  public ExecutorException(Schema schema, PreparedStatement preparedStatement, Throwable cause) {
    super(
        new StringBuilder(schema.toString())
            .append("Using query: ")
            .append(preparedStatement.toString())
            .append("\n")
            .toString(),
        cause);
  }

  public ExecutorException(String message) {
    super(message);
  }
}
