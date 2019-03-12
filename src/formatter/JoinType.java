package formatter;

public enum JoinType {
    COMMA(", "),
    AND(" AND "),
    OR(" OR ");

    private final String content;

    JoinType(String s) {
        this.content = s;
    }

    public String getContent() {
        return content;
    }
}
