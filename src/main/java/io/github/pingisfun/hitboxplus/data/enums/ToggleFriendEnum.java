package io.github.pingisfun.hitboxplus.data.enums;

public enum ToggleFriendEnum {
    FRIEND("friend"),
    NEUTRAL("neutral"),
    ENEMY("enemy");

    private final String stringName;

    ToggleFriendEnum(String string) {
        this.stringName = string;
    }

    public String getStringName() {
        return stringName;
    }
    public static ToggleFriendEnum fromString(String name) {
        return fromString(name, false);
    }

    public static ToggleFriendEnum fromString(String name, boolean ignoreCase) {
        name = ignoreCase ? name.toLowerCase() : name;
        for (ToggleFriendEnum value : ToggleFriendEnum.values()) {
            if (value.stringName.equals(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + name);
    }
}
