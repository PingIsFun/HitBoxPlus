package io.github.pingisfun.hitboxplus.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HitBoxPlusConfigTest {
    @Test
    void missingStyleFieldsNormalizeToDisplayDefaults() {
        HitboxColorConfig style = new HitboxColorConfig(12, 34, 56);
        HitBoxPlusConfig config = new HitBoxPlusConfig();

        style.normalize();

        assertEquals(false, config.usePlayerTeamColors);
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

    @Test
    void playerRelationsMoveBetweenFriendEnemyAndNeutral() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();

        config.setPlayerRelation("Steve", PlayerRelation.FRIEND);
        assertEquals(PlayerRelation.FRIEND, config.playerRelation("steve"));

        config.setPlayerRelation("Steve", PlayerRelation.ENEMY);
        assertEquals(PlayerRelation.ENEMY, config.playerRelation("STEVE"));
        assertEquals(false, config.friends.contains("Steve"));

        config.setPlayerRelation("Steve", null);
        assertEquals(null, config.playerRelation("Steve"));
        assertEquals(false, config.enemies.contains("Steve"));

        config.setPlayerRelation("Alex", PlayerRelation.FRIEND);
        config.removePlayerRelation("alex", PlayerRelation.FRIEND);
        assertEquals(null, config.playerRelation("Alex"));
    }
}
