package exception;

import schema.Schema;

public class NoRowsAffectedException extends ExecutorException {
    public NoRowsAffectedException(Schema schema, String statement) {
        super("No rows were affected during an update.", schema, statement);
    }

}
