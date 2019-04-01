package enums;

public enum RoleName {

    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    String type;

    RoleName(String type) {
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
