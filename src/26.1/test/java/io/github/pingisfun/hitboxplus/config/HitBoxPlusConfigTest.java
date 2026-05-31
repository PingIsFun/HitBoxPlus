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

    @Test
    void unknownEntityOverridesSurviveNormalization() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();
        EntityHitboxConfig override = new EntityHitboxConfig(true, new HitboxColorConfig(12, 34, 56));
        config.entityOverrides.put("minecraft:copper_golem", override);

        config.normalize();

        assertEquals(true, config.entityOverrides.containsKey("minecraft:copper_golem"));
        assertEquals(12, config.entityOverrides.get("minecraft:copper_golem").color.red);
    }

    @Test
    void groupWithMissingColorUsesGroupDefaultColor() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();
        config.groupHitboxes.put(EntityGroup.HOSTILE, new GroupHitboxConfig(true, null));

        config.normalize();

        HitboxColorConfig hostile = config.groupHitboxes.get(EntityGroup.HOSTILE).color;
        assertEquals(140, hostile.red);
        assertEquals(16, hostile.green);
        assertEquals(7, hostile.blue);
        assertEquals(true, hostile.showHitbox);
    }

    @Test
    void groupWithEmptyDeserializedColorUsesGroupDefaultColor() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();
        HitboxColorConfig emptyColor = new HitboxColorConfig(0, 0, 0);
        emptyColor.showHitbox = false;
        emptyColor.hitboxThickness = 0.0F;
        emptyColor.hitboxPattern = null;
        emptyColor.showEyeLine = false;
        emptyColor.showLookDirection = false;
        config.groupHitboxes.put(EntityGroup.PASSIVE, new GroupHitboxConfig(true, emptyColor));

        config.normalize();

        HitboxColorConfig passive = config.groupHitboxes.get(EntityGroup.PASSIVE).color;
        assertEquals(143, passive.red);
        assertEquals(163, passive.green);
        assertEquals(30, passive.blue);
        assertEquals(true, passive.showHitbox);
    }
}
