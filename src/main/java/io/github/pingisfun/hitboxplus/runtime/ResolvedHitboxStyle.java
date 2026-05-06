package io.github.pingisfun.hitboxplus.runtime;

import io.github.pingisfun.hitboxplus.config.HitboxColorConfig;
import io.github.pingisfun.hitboxplus.config.HitboxPattern;

public record ResolvedHitboxStyle(
        float red,
        float green,
        float blue,
        boolean showHitbox,
        float hitboxThickness,
        HitboxPattern hitboxPattern,
        boolean showEyeLine,
        boolean showLookDirection
) {
    public static ResolvedHitboxStyle fromConfig(HitboxColorConfig color) {
        return new ResolvedHitboxStyle(
                color.red / 255.0F,
                color.green / 255.0F,
                color.blue / 255.0F,
                color.showHitbox,
                color.hitboxThickness,
                color.hitboxPattern,
                color.showEyeLine,
                color.showLookDirection
        );
    }

    public int opaqueArgb() {
        int redInt = Math.round(red * 255.0F);
        int greenInt = Math.round(green * 255.0F);
        int blueInt = Math.round(blue * 255.0F);
        return 0xFF000000 | redInt << 16 | greenInt << 8 | blueInt;
    }

    public ResolvedHitboxStyle withRgb(int rgb) {
        return new ResolvedHitboxStyle(
                (rgb >> 16 & 0xFF) / 255.0F,
                (rgb >> 8 & 0xFF) / 255.0F,
                (rgb & 0xFF) / 255.0F,
                showHitbox,
                hitboxThickness,
                hitboxPattern,
                showEyeLine,
                showLookDirection
        );
    }
}
