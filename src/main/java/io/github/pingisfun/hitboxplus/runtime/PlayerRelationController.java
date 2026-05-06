package io.github.pingisfun.hitboxplus.runtime;

import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfig;
import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfigManager;
import io.github.pingisfun.hitboxplus.config.PlayerRelation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;

import java.util.List;

public final class PlayerRelationController {
    private PlayerRelationController() {
    }

    public static PlayerRelation set(String playerName, PlayerRelation relation) {
        HitBoxPlusConfig config = HitBoxPlusConfigManager.config();
        config.setPlayerRelation(playerName, relation);
        HitBoxPlusConfigManager.save();
        return relation;
    }

    public static void remove(String playerName, PlayerRelation relation) {
        HitBoxPlusConfig config = HitBoxPlusConfigManager.config();
        config.removePlayerRelation(playerName, relation);
        HitBoxPlusConfigManager.save();
    }

    public static PlayerRelation cycle(PlayerEntity player) {
        String playerName = player.getName().getString();
        HitBoxPlusConfig config = HitBoxPlusConfigManager.config();
        PlayerRelation current = config.playerRelation(playerName);
        PlayerRelation next;
        if (current == null) {
            next = PlayerRelation.FRIEND;
        } else if (current == PlayerRelation.FRIEND) {
            next = PlayerRelation.ENEMY;
        } else {
            next = null;
        }

        config.setPlayerRelation(playerName, next);
        HitBoxPlusConfigManager.save();
        return next;
    }

    public static void announce(String playerName, PlayerRelation relation) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        client.player.sendMessage(relationText(playerName, relation), false);
    }

    public static Text relationText(String playerName, PlayerRelation relation) {
        String relationName = relation == null ? "neutral" : relation.name().toLowerCase(java.util.Locale.ROOT);
        Formatting relationColor = relation == PlayerRelation.FRIEND
                ? Formatting.GREEN
                : relation == PlayerRelation.ENEMY ? Formatting.RED : Formatting.GRAY;
        return prefix()
                .append(Text.literal(playerName).formatted(Formatting.YELLOW))
                .append(Text.literal(" is now ").formatted(Formatting.GRAY))
                .append(Text.literal(relationName).formatted(relationColor))
                .append(Text.literal(".").formatted(Formatting.GRAY));
    }

    public static Text removedText(String playerName, PlayerRelation relation) {
        String relationName = relation.name().toLowerCase(java.util.Locale.ROOT);
        Formatting relationColor = relation == PlayerRelation.FRIEND ? Formatting.GREEN : Formatting.RED;
        return prefix()
                .append(Text.literal("Removed ").formatted(Formatting.GRAY))
                .append(Text.literal(playerName).formatted(Formatting.YELLOW))
                .append(Text.literal(" from ").formatted(Formatting.GRAY))
                .append(Text.literal(relationName + "s").formatted(relationColor))
                .append(Text.literal(".").formatted(Formatting.GRAY));
    }

    public static Text listText(PlayerRelation relation, List<String> players) {
        String relationName = relation.name().toLowerCase(java.util.Locale.ROOT) + "s";
        Formatting relationColor = relation == PlayerRelation.FRIEND ? Formatting.GREEN : Formatting.RED;
        if (players.isEmpty()) {
            return prefix()
                    .append(Text.literal("No ").formatted(Formatting.GRAY))
                    .append(Text.literal(relationName).formatted(relationColor))
                    .append(Text.literal(" configured.").formatted(Formatting.GRAY));
        }

        return prefix()
                .append(Text.literal(relationName + ": ").formatted(relationColor))
                .append(Text.literal(String.join(", ", players)).formatted(Formatting.YELLOW));
    }

    private static net.minecraft.text.MutableText prefix() {
        return Text.literal("[HitBox+] ").formatted(Formatting.AQUA);
    }
}
