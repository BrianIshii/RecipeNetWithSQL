package exception;

import schema.Schema;

public class NoRowsAffectedException extends ExecutorException {
    private static final String MSG = "No rows were affected during an update.";
    public NoRowsAffectedException(Schema schema, String query) {
        super(MSG, schema, query);
    }
    public NoRowsAffectedException(String query) {
        super(new StringBuilder(MSG).append("Query: ").append(query).append("\n").toString());
    }
}
