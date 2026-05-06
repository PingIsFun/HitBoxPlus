package io.github.pingisfun.hitboxplus.runtime;

import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfig;
import io.github.pingisfun.hitboxplus.config.GroupHitboxConfig;
import io.github.pingisfun.hitboxplus.config.PlayerRelation;
import io.github.pingisfun.hitboxplus.data.EntityGroupData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Locale;

public final class RuntimeHitboxLookup {
    private final boolean enabled;
    private final ResolvedHitboxStyle defaultStyle;
    private final ResolvedHitboxStyle selfPlayerStyle;
    private final ResolvedHitboxStyle neutralPlayerStyle;
    private final ResolvedHitboxStyle friendPlayerStyle;
    private final ResolvedHitboxStyle enemyPlayerStyle;
    private final Map<EntityType<?>, ResolvedHitboxStyle> entityStyles;
    private final Map<String, PlayerRelation> playerRelations;
    private final boolean usePlayerTeamColors;

    private RuntimeHitboxLookup(
            boolean enabled,
            ResolvedHitboxStyle defaultStyle,
            ResolvedHitboxStyle selfPlayerStyle,
            ResolvedHitboxStyle neutralPlayerStyle,
            ResolvedHitboxStyle friendPlayerStyle,
            ResolvedHitboxStyle enemyPlayerStyle,
            Map<EntityType<?>, ResolvedHitboxStyle> entityStyles,
            Map<String, PlayerRelation> playerRelations,
            boolean usePlayerTeamColors
    ) {
        this.enabled = enabled;
        this.defaultStyle = defaultStyle;
        this.selfPlayerStyle = selfPlayerStyle;
        this.neutralPlayerStyle = neutralPlayerStyle;
        this.friendPlayerStyle = friendPlayerStyle;
        this.enemyPlayerStyle = enemyPlayerStyle;
        this.entityStyles = entityStyles;
        this.playerRelations = playerRelations;
        this.usePlayerTeamColors = usePlayerTeamColors;
    }

    public static RuntimeHitboxLookup compile(HitBoxPlusConfig config) {
        ResolvedHitboxStyle defaultStyle = ResolvedHitboxStyle.fromConfig(config.defaultHitbox);
        Map<EntityType<?>, ResolvedHitboxStyle> entityStyles = new IdentityHashMap<>();

        Registries.ENTITY_TYPE.forEach(entityType -> entityStyles.put(entityType, defaultStyle));
        EntityGroupData.ENTITY_GROUPS.forEach((entityType, group) -> {
            GroupHitboxConfig groupConfig = config.groupHitboxes.get(group);
            if (groupConfig != null && groupConfig.enabled) {
                entityStyles.put(entityType, ResolvedHitboxStyle.fromConfig(groupConfig.color));
            }
        });

        config.entityOverrides.forEach((id, override) -> {
            Identifier identifier = Identifier.of(id);
            if (!Registries.ENTITY_TYPE.containsId(identifier)) {
                return;
            }

            EntityType<?> entityType = Registries.ENTITY_TYPE.get(identifier);
            entityStyles.put(entityType, ResolvedHitboxStyle.fromConfig(override.color));
        });

        Map<String, PlayerRelation> playerRelations = new HashMap<>();
        config.friends.forEach(name -> playerRelations.put(normalizePlayerName(name), PlayerRelation.FRIEND));
        config.enemies.forEach(name -> playerRelations.put(normalizePlayerName(name), PlayerRelation.ENEMY));

        return new RuntimeHitboxLookup(
                config.isEnabled(),
                defaultStyle,
                ResolvedHitboxStyle.fromConfig(config.selfPlayer),
                ResolvedHitboxStyle.fromConfig(config.neutralPlayer),
                ResolvedHitboxStyle.fromConfig(config.friendPlayer),
                ResolvedHitboxStyle.fromConfig(config.enemyPlayer),
                Collections.unmodifiableMap(entityStyles),
                Collections.unmodifiableMap(playerRelations),
                config.usePlayerTeamColors
        );
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ResolvedHitboxStyle forEntityType(EntityType<?> entityType) {
        return entityStyles.getOrDefault(entityType, defaultStyle);
    }

    public ResolvedHitboxStyle forPlayerName(String playerName) {
        PlayerRelation relation = playerRelations.get(normalizePlayerName(playerName));
        if (relation == PlayerRelation.FRIEND) {
            return friendPlayerStyle;
        }
        if (relation == PlayerRelation.ENEMY) {
            return enemyPlayerStyle;
        }
        return neutralPlayerStyle;
    }

    public ResolvedHitboxStyle forPlayer(PlayerEntity player) {
        PlayerRelation relation = playerRelations.get(normalizePlayerName(player.getName().getString()));
        if (relation == PlayerRelation.FRIEND) {
            return friendPlayerStyle;
        }
        if (relation == PlayerRelation.ENEMY) {
            return enemyPlayerStyle;
        }

        Integer teamColor = usePlayerTeamColors ? teamColor(player) : null;
        if (teamColor != null) {
            return neutralPlayerStyle.withRgb(teamColor);
        }

        return neutralPlayerStyle;
    }

    public ResolvedHitboxStyle selfPlayerStyle() {
        return selfPlayerStyle;
    }

    public ResolvedHitboxStyle neutralPlayerStyle() {
        return neutralPlayerStyle;
    }

    public ResolvedHitboxStyle friendPlayerStyle() {
        return friendPlayerStyle;
    }

    public ResolvedHitboxStyle enemyPlayerStyle() {
        return enemyPlayerStyle;
    }

    private static String normalizePlayerName(String playerName) {
        return playerName.strip().toLowerCase(Locale.ROOT);
    }

    private static Integer teamColor(PlayerEntity player) {
        Team team = player.getScoreboardTeam();
        if (team == null) {
            return null;
        }
        return team.getColor().getColorValue();
    }
}
