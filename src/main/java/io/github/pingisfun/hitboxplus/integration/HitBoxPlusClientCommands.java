package io.github.pingisfun.hitboxplus.integration;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.pingisfun.hitboxplus.config.PlayerRelation;
import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfigManager;
import io.github.pingisfun.hitboxplus.runtime.PlayerRelationController;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.CommandSource;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.stream.Stream;

public final class HitBoxPlusClientCommands {
    private HitBoxPlusClientCommands() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("hitboxplus")
                        .then(relationCommand("friend", PlayerRelation.FRIEND))
                        .then(relationCommand("enemy", PlayerRelation.ENEMY))
        ));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource> relationCommand(
            String name,
            PlayerRelation relation
    ) {
        return ClientCommandManager.literal(name)
                .then(ClientCommandManager.literal("add")
                        .then(ClientCommandManager.argument("player", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(onlinePlayerNames(context), builder))
                                .executes(context -> setRelation(context, relation))))
                .then(ClientCommandManager.literal("remove")
                        .then(ClientCommandManager.argument("player", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(configuredPlayerNames(relation), builder))
                                .executes(context -> removeRelation(context, relation))))
                .then(ClientCommandManager.literal("list")
                        .executes(context -> listRelation(context, relation)));
    }

    private static int setRelation(
            CommandContext<FabricClientCommandSource> context,
            PlayerRelation relation
    ) {
        String playerName = StringArgumentType.getString(context, "player");
        PlayerRelationController.set(playerName, relation);
        context.getSource().sendFeedback(PlayerRelationController.relationText(playerName, relation));
        return 1;
    }

    private static int removeRelation(CommandContext<FabricClientCommandSource> context, PlayerRelation relation) {
        String playerName = StringArgumentType.getString(context, "player");
        PlayerRelationController.remove(playerName, relation);
        context.getSource().sendFeedback(PlayerRelationController.removedText(playerName, relation));
        return 1;
    }

    private static int listRelation(CommandContext<FabricClientCommandSource> context, PlayerRelation relation) {
        context.getSource().sendFeedback(PlayerRelationController.listText(relation, configuredPlayerNames(relation)));
        return 1;
    }

    private static Stream<String> onlinePlayerNames(CommandContext<FabricClientCommandSource> context) {
        if (context.getSource().getClient().player == null) {
            return Stream.empty();
        }

        return context.getSource().getClient().player.networkHandler.getListedPlayerListEntries().stream()
                .map(entry -> profileName(entry.getProfile()));
    }

    private static String profileName(GameProfile profile) {
        //? if >=1.21.9 {
        return profile.name();
        //?} else {
        /*return profile.getName();
        *///?}
    }

    private static List<String> configuredPlayerNames(PlayerRelation relation) {
        return relation == PlayerRelation.FRIEND
                ? HitBoxPlusConfigManager.config().friends
                : HitBoxPlusConfigManager.config().enemies;
    }
}
