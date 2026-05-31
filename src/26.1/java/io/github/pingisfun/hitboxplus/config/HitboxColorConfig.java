package io.github.pingisfun.hitboxplus.config;

import java.awt.Color;

public final class HitboxColorConfig {
    public static final float DEFAULT_HITBOX_THICKNESS = 2.5F;
    public static final float MIN_HITBOX_THICKNESS = 0.5F;
    public static final float MAX_HITBOX_THICKNESS = 10.0F;

    public int red;
    public int green;
    public int blue;
    public boolean showHitbox = true;
    public float hitboxThickness = DEFAULT_HITBOX_THICKNESS;
    public HitboxPattern hitboxPattern = HitboxPattern.FULL;
    public boolean showEyeLine = true;
    public boolean showLookDirection = true;

    public HitboxColorConfig() {
        this(255, 255, 255);
    }

    public HitboxColorConfig(int red, int green, int blue) {
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
    }

    public Color toAwtColor() {
        return new Color(red, green, blue);
    }

    public void setAwtColor(Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
    }

    public void normalize() {
        red = clamp(red);
        green = clamp(green);
        blue = clamp(blue);
        hitboxThickness = clamp(hitboxThickness, MIN_HITBOX_THICKNESS, MAX_HITBOX_THICKNESS);
        if (hitboxPattern == null) {
            hitboxPattern = HitboxPattern.FULL;
        }
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static float clamp(float value, float min, float max) {
        if (!Float.isFinite(value)) {
            return DEFAULT_HITBOX_THICKNESS;
        }
        return Math.max(min, Math.min(max, value));
    }
}
