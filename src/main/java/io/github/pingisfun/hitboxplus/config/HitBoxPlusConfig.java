package io.github.pingisfun.hitboxplus.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerialEntry
public final class HitBoxPlusConfig {
    public int configVersion = 1;
    private boolean enabled = true;
    public HitboxColorConfig defaultHitbox = new HitboxColorConfig(255, 255, 255);
    public HitboxColorConfig selfPlayer = new HitboxColorConfig(255, 255, 255);
    public HitboxColorConfig neutralPlayer = new HitboxColorConfig(255, 255, 255);
    public HitboxColorConfig friendPlayer = new HitboxColorConfig(10, 64, 12);
    public HitboxColorConfig enemyPlayer = new HitboxColorConfig(255, 63, 51);
    public boolean usePlayerTeamColors = false;
    public Map<EntityGroup, GroupHitboxConfig> groupHitboxes = createDefaultGroupHitboxes();
    public Map<String, EntityHitboxConfig> entityOverrides = new HashMap<>();
    public List<String> friends = new ArrayList<>();
    public List<String> enemies = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void normalize() {
        configVersion = 1;
        defaultHitbox = normalizeColor(defaultHitbox, new HitboxColorConfig(255, 255, 255));
        selfPlayer = normalizeColor(selfPlayer, new HitboxColorConfig(255, 255, 255));
        neutralPlayer = normalizeColor(neutralPlayer, new HitboxColorConfig(255, 255, 255));
        friendPlayer = normalizeColor(friendPlayer, new HitboxColorConfig(10, 64, 12));
        enemyPlayer = normalizeColor(enemyPlayer, new HitboxColorConfig(255, 63, 51));
        groupHitboxes = normalizeGroupHitboxes(groupHitboxes);
        entityOverrides = normalizeEntityOverrides(entityOverrides);
        friends = normalizePlayerNames(friends);
        enemies = normalizePlayerNames(enemies);
        friends.removeAll(enemies);
    }

    private static HitboxColorConfig normalizeColor(HitboxColorConfig color, HitboxColorConfig fallback) {
        HitboxColorConfig normalized = color == null ? fallback : color;
        normalized.normalize();
        return normalized;
    }

    private static Map<String, EntityHitboxConfig> normalizeEntityOverrides(Map<String, EntityHitboxConfig> overrides) {
        Map<String, EntityHitboxConfig> normalized = new HashMap<>();
        if (overrides == null) {
            return normalized;
        }

        for (Map.Entry<String, EntityHitboxConfig> entry : overrides.entrySet()) {
            Identifier id = Identifier.tryParse(entry.getKey());
            if (id == null) {
                continue;
            }

            EntityHitboxConfig config = entry.getValue() == null ? new EntityHitboxConfig() : entry.getValue();
            config.normalize();
            if (config.enabled) {
                normalized.put(id.toString(), config);
            }
        }

        return normalized;
    }

    public EntityHitboxConfig entityOverride(String entityId) {
        return entityOverrides.computeIfAbsent(entityId, ignored -> new EntityHitboxConfig());
    }

    public boolean isEntityOverrideEnabled(String entityId) {
        EntityHitboxConfig override = entityOverrides.get(entityId);
        return override != null && override.enabled;
    }

    public PlayerRelation playerRelation(String playerName) {
        if (containsPlayerName(friends, playerName)) {
            return PlayerRelation.FRIEND;
        }
        if (containsPlayerName(enemies, playerName)) {
            return PlayerRelation.ENEMY;
        }
        return null;
    }

    public void setPlayerRelation(String playerName, PlayerRelation relation) {
        String normalizedName = playerName.strip();
        if (normalizedName.isEmpty()) {
            return;
        }

        removePlayerName(friends, normalizedName);
        removePlayerName(enemies, normalizedName);
        if (relation == PlayerRelation.FRIEND) {
            friends.add(normalizedName);
        } else if (relation == PlayerRelation.ENEMY) {
            enemies.add(normalizedName);
        }
    }

    public void removePlayerRelation(String playerName, PlayerRelation relation) {
        String normalizedName = playerName.strip();
        if (normalizedName.isEmpty()) {
            return;
        }

        if (relation == PlayerRelation.FRIEND) {
            removePlayerName(friends, normalizedName);
        } else if (relation == PlayerRelation.ENEMY) {
            removePlayerName(enemies, normalizedName);
        }
    }

    public void setEntityOverrideEnabled(String entityId, boolean enabled) {
        EntityHitboxConfig override = entityOverride(entityId);
        override.enabled = enabled;
        if (!enabled) {
            entityOverrides.remove(entityId);
        }
    }

    public void enableEntityOverride(String entityId) {
        EntityHitboxConfig override = entityOverride(entityId);
        override.enabled = true;
    }

    public void removeEntityOverride(String entityId) {
        entityOverrides.remove(entityId);
    }

    public void setEntityOverride(String entityId, java.awt.Color color) {
        EntityHitboxConfig override = entityOverride(entityId);
        override.color.setAwtColor(color);
    }

    public java.awt.Color getEntityOverrideColor(String entityId) {
        EntityHitboxConfig override = entityOverrides.get(entityId);
        return override == null ? defaultHitbox.toAwtColor() : override.color.toAwtColor();
    }

    public static Map<EntityGroup, GroupHitboxConfig> createDefaultGroupHitboxes() {
        Map<EntityGroup, GroupHitboxConfig> defaults = new EnumMap<>(EntityGroup.class);
        defaults.put(EntityGroup.PASSIVE, new GroupHitboxConfig(true, new HitboxColorConfig(143, 163, 30)));
        defaults.put(EntityGroup.HOSTILE, new GroupHitboxConfig(true, new HitboxColorConfig(140, 16, 7)));
        defaults.put(EntityGroup.BOSS, new GroupHitboxConfig(true, new HitboxColorConfig(145, 18, 188)));
        defaults.put(EntityGroup.PROJECTILE, new GroupHitboxConfig(false, new HitboxColorConfig()));
        defaults.put(EntityGroup.EFFECT, new GroupHitboxConfig(false, new HitboxColorConfig()));
        defaults.put(EntityGroup.VEHICLE, new GroupHitboxConfig(false, new HitboxColorConfig()));
        defaults.put(EntityGroup.MISC, new GroupHitboxConfig(false, new HitboxColorConfig()));
        return defaults;
    }

    private static Map<EntityGroup, GroupHitboxConfig> normalizeGroupHitboxes(Map<EntityGroup, GroupHitboxConfig> groupHitboxes) {
        Map<EntityGroup, GroupHitboxConfig> normalized = createDefaultGroupHitboxes();
        if (groupHitboxes == null) {
            return normalized;
        }

        groupHitboxes.forEach((group, config) -> {
            if (group == null || config == null) {
                return;
            }

            GroupHitboxConfig fallback = normalized.getOrDefault(group, new GroupHitboxConfig());
            config.normalize(copyColor(fallback.color));
            normalized.put(group, config);
        });

        return normalized;
    }

    private static HitboxColorConfig copyColor(HitboxColorConfig color) {
        HitboxColorConfig copy = new HitboxColorConfig(color.red, color.green, color.blue);
        copy.showHitbox = color.showHitbox;
        copy.hitboxThickness = color.hitboxThickness;
        copy.hitboxPattern = color.hitboxPattern;
        copy.showEyeLine = color.showEyeLine;
        copy.showLookDirection = color.showLookDirection;
        return copy;
    }

    private static List<String> normalizePlayerNames(List<String> names) {
        if (names == null) {
            return new ArrayList<>();
        }

        return names.stream()
                .map(String::strip)
                .filter(name -> !name.isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    private static boolean containsPlayerName(List<String> names, String playerName) {
        return names.stream().anyMatch(name -> name.equalsIgnoreCase(playerName));
    }

    private static void removePlayerName(List<String> names, String playerName) {
        names.removeIf(name -> name.equalsIgnoreCase(playerName));
    }
}
