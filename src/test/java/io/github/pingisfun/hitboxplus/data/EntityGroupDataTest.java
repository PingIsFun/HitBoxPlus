package io.github.pingisfun.hitboxplus.data;

import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfig;
import io.github.pingisfun.hitboxplus.config.EntityGroup;
import io.github.pingisfun.hitboxplus.config.EntityHitboxConfig;
import io.github.pingisfun.hitboxplus.config.GroupHitboxConfig;
import io.github.pingisfun.hitboxplus.config.HitboxColorConfig;
import io.github.pingisfun.hitboxplus.config.HitboxPattern;
import io.github.pingisfun.hitboxplus.runtime.ResolvedHitboxStyle;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxLookup;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EntityGroupDataTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test
    void classifiesEveryNonPlayerEntityType() {
        List<EntityType<?>> missing = new ArrayList<>(Registries.ENTITY_TYPE.stream().toList());
        missing.remove(EntityType.PLAYER);
        missing.removeAll(EntityGroupData.ENTITY_GROUPS.keySet());

        assertEquals(List.of(), missing);
    }

    @Test
    void compiledLookupContainsEveryEntityType() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();
        config.normalize();
        RuntimeHitboxLookup lookup = RuntimeHitboxLookup.compile(config);

        Registries.ENTITY_TYPE.forEach(entityType -> assertNotNull(lookup.forEntityType(entityType)));
    }

    @Test
    void compiledLookupIncludesDisplayFields() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();
        config.defaultHitbox.showHitbox = false;
        config.defaultHitbox.hitboxThickness = 4.5F;
        config.defaultHitbox.hitboxPattern = HitboxPattern.DOTTED;
        config.defaultHitbox.showEyeLine = false;
        config.defaultHitbox.showLookDirection = false;
        config.selfPlayer.hitboxPattern = HitboxPattern.DOTTED;
        config.selfPlayer.hitboxThickness = 6.0F;
        config.neutralPlayer.hitboxThickness = 5.0F;
        config.neutralPlayer.showHitbox = false;
        config.friendPlayer.showLookDirection = false;
        config.enemyPlayer.showEyeLine = false;
        config.normalize();

        RuntimeHitboxLookup lookup = RuntimeHitboxLookup.compile(config);
        ResolvedHitboxStyle style = lookup.forEntityType(EntityType.ARMOR_STAND);

        assertEquals(false, style.showHitbox());
        assertEquals(4.5F, style.hitboxThickness());
        assertEquals(HitboxPattern.DOTTED, style.hitboxPattern());
        assertEquals(false, style.showEyeLine());
        assertEquals(false, style.showLookDirection());
        assertEquals(HitboxPattern.DOTTED, lookup.selfPlayerStyle().hitboxPattern());
        assertEquals(6.0F, lookup.selfPlayerStyle().hitboxThickness());
        assertEquals(5.0F, lookup.neutralPlayerStyle().hitboxThickness());
        assertEquals(false, lookup.neutralPlayerStyle().showHitbox());
        assertEquals(false, lookup.friendPlayerStyle().showLookDirection());
        assertEquals(false, lookup.enemyPlayerStyle().showEyeLine());
    }

    @Test
    void teamColorReplacementKeepsNeutralPlayerDisplaySettings() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();
        config.neutralPlayer.hitboxThickness = 4.0F;
        config.neutralPlayer.hitboxPattern = HitboxPattern.DOTTED;
        config.neutralPlayer.showEyeLine = false;
        config.normalize();

        ResolvedHitboxStyle style = RuntimeHitboxLookup.compile(config).neutralPlayerStyle().withRgb(0x123456);

        assertEquals(0xFF123456, style.opaqueArgb());
        assertEquals(4.0F, style.hitboxThickness());
        assertEquals(HitboxPattern.DOTTED, style.hitboxPattern());
        assertEquals(false, style.showEyeLine());
    }

    @Test
    void entityOverrideDisplaySettingsOverrideGroupAndDefaultSettings() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();
        config.defaultHitbox.hitboxPattern = HitboxPattern.FULL;
        config.groupHitboxes.put(EntityGroup.HOSTILE, new GroupHitboxConfig(true, style(HitboxPattern.FULL, 3.0F)));
        config.entityOverrides.put("minecraft:zombie", new EntityHitboxConfig(true, style(HitboxPattern.DOTTED, 7.0F)));
        config.normalize();

        RuntimeHitboxLookup lookup = RuntimeHitboxLookup.compile(config);
        ResolvedHitboxStyle skeletonStyle = lookup.forEntityType(EntityType.SKELETON);
        ResolvedHitboxStyle zombieStyle = lookup.forEntityType(EntityType.ZOMBIE);

        assertEquals(HitboxPattern.FULL, skeletonStyle.hitboxPattern());
        assertEquals(3.0F, skeletonStyle.hitboxThickness());
        assertEquals(HitboxPattern.DOTTED, zombieStyle.hitboxPattern());
        assertEquals(7.0F, zombieStyle.hitboxThickness());
    }

    @Test
    void disabledGroupUsesDefaultStyle() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();
        config.defaultHitbox = new HitboxColorConfig(1, 2, 3);
        config.defaultHitbox.hitboxThickness = 4.0F;
        config.groupHitboxes.put(EntityGroup.HOSTILE, new GroupHitboxConfig(false, new HitboxColorConfig(140, 16, 7)));
        config.normalize();

        ResolvedHitboxStyle skeletonStyle = RuntimeHitboxLookup.compile(config).forEntityType(EntityType.SKELETON);

        assertEquals(0xFF010203, skeletonStyle.opaqueArgb());
        assertEquals(4.0F, skeletonStyle.hitboxThickness());
    }

    private static HitboxColorConfig style(HitboxPattern pattern, float thickness) {
        HitboxColorConfig style = new HitboxColorConfig();
        style.hitboxPattern = pattern;
        style.hitboxThickness = thickness;
        return style;
    }
}
