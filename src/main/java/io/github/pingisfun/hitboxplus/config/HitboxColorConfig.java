package io.github.pingisfun.hitboxplus.config;

import java.awt.Color;

public final class HitboxColorConfig {
    public int red;
    public int green;
    public int blue;

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
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
