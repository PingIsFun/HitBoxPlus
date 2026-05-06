package io.github.pingisfun.hitboxplus.config;

public enum EntityGroup {
    PASSIVE("Passive"),
    HOSTILE("Hostile"),
    BOSS("Boss"),
    PROJECTILE("Projectile"),
    EFFECT("Effect"),
    VEHICLE("Vehicle"),
    MISC("Misc");

    private final String displayName;

    EntityGroup(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
