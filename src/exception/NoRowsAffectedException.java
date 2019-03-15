package exception;

import schema.Schema;

import java.sql.PreparedStatement;

public class NoRowsAffectedException extends ExecutorException {
    private static final String MSG = "No rows were affected during an update.";
    public NoRowsAffectedException(Schema schema, PreparedStatement preparedStatement) {
        super(MSG, schema, preparedStatement);
    }
    public NoRowsAffectedException(PreparedStatement preparedStatement) {
        super(new StringBuilder(MSG).append("\nQuery: ").append(preparedStatement.toString()).append("\n").toString());
    }
}
