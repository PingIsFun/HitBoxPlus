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
        if (color == null || isUninitialized(color)) {
            color = fallback;
        }

        color.normalize();
    }

    private static boolean isUninitialized(HitboxColorConfig color) {
        return color.red == 0
                && color.green == 0
                && color.blue == 0
                && !color.showHitbox
                && color.hitboxThickness == 0.0F
                && color.hitboxPattern == null
                && !color.showEyeLine
                && !color.showLookDirection;
    }
}
