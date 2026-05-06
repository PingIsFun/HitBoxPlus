package io.github.pingisfun.hitboxplus.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HitBoxPlusConfigTest {
    @Test
    void missingStyleFieldsNormalizeToDisplayDefaults() {
        HitboxColorConfig style = new HitboxColorConfig(12, 34, 56);

        style.normalize();

        assertTrue(style.showHitbox);
        assertEquals(HitboxColorConfig.DEFAULT_HITBOX_THICKNESS, style.hitboxThickness);
        assertEquals(HitboxPattern.FULL, style.hitboxPattern);
        assertTrue(style.showEyeLine);
        assertTrue(style.showLookDirection);
    }

    @Test
    void thicknessClampsToAllowedRange() {
        HitboxColorConfig tooThin = new HitboxColorConfig();
        tooThin.hitboxThickness = -1.0F;
        tooThin.normalize();

        HitboxColorConfig tooThick = new HitboxColorConfig();
        tooThick.hitboxThickness = 20.0F;
        tooThick.normalize();

        assertEquals(HitboxColorConfig.MIN_HITBOX_THICKNESS, tooThin.hitboxThickness);
        assertEquals(HitboxColorConfig.MAX_HITBOX_THICKNESS, tooThick.hitboxThickness);
    }

    @Test
    void nullPatternNormalizesToFull() {
        HitboxColorConfig style = new HitboxColorConfig();
        style.hitboxPattern = null;

        style.normalize();

        assertEquals(HitboxPattern.FULL, style.hitboxPattern);
    }
}
