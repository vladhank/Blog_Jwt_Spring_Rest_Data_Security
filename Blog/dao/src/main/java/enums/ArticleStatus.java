package enums;

public enum ArticleStatus {
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE");

    String type;

    ArticleStatus(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


    @Override
    public String toString() {
        return this.type;
    }

    public String getName() {
        return this.name();
    }
}
