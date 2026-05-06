package io.github.pingisfun.hitboxplus.config;

public final class EntityHitboxConfig {
    public boolean enabled;
    public HitboxColorConfig color;

    public EntityHitboxConfig() {
        this(false, new HitboxColorConfig());
    }

    public EntityHitboxConfig(boolean enabled, HitboxColorConfig color) {
        this.enabled = enabled;
        this.color = color;
    }

    public void normalize() {
        if (color == null) {
            color = new HitboxColorConfig();
        }
        color.normalize();
    }
}
