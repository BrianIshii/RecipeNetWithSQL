package RecipeNet.formatter;

/**
 * Currently not in use.
 */
public enum CompareType {
    EQUAL("="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_OR_EQUAL("<="),
    GREATER_OR_EQUAL(">=");

    private String content;

    private CompareType(String content) {
        this.content = content;
    }
}
