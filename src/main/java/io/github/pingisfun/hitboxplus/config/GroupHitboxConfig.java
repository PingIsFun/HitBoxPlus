package io.github.pingisfun.hitboxplus.config;

public final class GroupHitboxConfig {
    public boolean enabled;
    public HitboxColorConfig color;

    public GroupHitboxConfig() {
        this(false, new HitboxColorConfig());
    }

    public GroupHitboxConfig(boolean enabled, HitboxColorConfig color) {
        this.enabled = enabled;
        this.color = color;
    }

    public void normalize(HitboxColorConfig fallback) {
        if (color == null) {
            color = fallback;
        }

        color.normalize();
    }
}
