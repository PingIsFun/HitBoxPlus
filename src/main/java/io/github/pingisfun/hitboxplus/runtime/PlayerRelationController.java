package io.github.pingisfun.hitboxplus.runtime;

import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfig;
import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfigManager;
import io.github.pingisfun.hitboxplus.config.PlayerRelation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

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

    public static PlayerRelation cycle(Player player) {
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
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        client.player.sendSystemMessage(relationText(playerName, relation));
    }

    public static Component relationText(String playerName, PlayerRelation relation) {
        String relationName = relation == null ? "neutral" : relation.name().toLowerCase(java.util.Locale.ROOT);
        ChatFormatting relationColor = relation == PlayerRelation.FRIEND
                ? ChatFormatting.GREEN
                : relation == PlayerRelation.ENEMY ? ChatFormatting.RED : ChatFormatting.GRAY;
        return prefix()
                .append(Component.literal(playerName).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" is now ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(relationName).withStyle(relationColor))
                .append(Component.literal(".").withStyle(ChatFormatting.GRAY));
    }

    public static Component removedText(String playerName, PlayerRelation relation) {
        String relationName = relation.name().toLowerCase(java.util.Locale.ROOT);
        ChatFormatting relationColor = relation == PlayerRelation.FRIEND ? ChatFormatting.GREEN : ChatFormatting.RED;
        return prefix()
                .append(Component.literal("Removed ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(playerName).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" from ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(relationName + "s").withStyle(relationColor))
                .append(Component.literal(".").withStyle(ChatFormatting.GRAY));
    }

    public static Component listText(PlayerRelation relation, List<String> players) {
        String relationName = relation.name().toLowerCase(java.util.Locale.ROOT) + "s";
        ChatFormatting relationColor = relation == PlayerRelation.FRIEND ? ChatFormatting.GREEN : ChatFormatting.RED;
        if (players.isEmpty()) {
            return prefix()
                    .append(Component.literal("No ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(relationName).withStyle(relationColor))
                    .append(Component.literal(" configured.").withStyle(ChatFormatting.GRAY));
        }

        return prefix()
                .append(Component.literal(relationName + ": ").withStyle(relationColor))
                .append(Component.literal(String.join(", ", players)).withStyle(ChatFormatting.YELLOW));
    }

    private static net.minecraft.network.chat.MutableComponent prefix() {
        return Component.literal("[HitBox+] ").withStyle(ChatFormatting.AQUA);
    }
}
