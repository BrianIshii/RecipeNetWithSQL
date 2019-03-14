package exception;

public class PreparedStatementCloseFailureException extends ExecutorException {
    public PreparedStatementCloseFailureException(Throwable throwable) {
        super("Issue occurred while attempting to close prepared statement", throwable);
    }
}
