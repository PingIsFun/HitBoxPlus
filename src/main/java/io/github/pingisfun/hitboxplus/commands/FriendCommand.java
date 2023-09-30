package io.github.pingisfun.hitboxplus.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.pingisfun.hitboxplus.ModConfig;
import io.github.pingisfun.hitboxplus.data.enums.ToggleFriendEnum;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;

import java.util.Arrays;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FriendCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("hitboxplus")
                .then(argument("action", StringArgumentType.string())
                        .suggests((context, builder) -> CommandSource.suggestMatching(Arrays.stream(ToggleFriendEnum.values()).map(ToggleFriendEnum::getStringName).toList(), builder))
                        .then(argument("player", StringArgumentType.string())
                                .suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
                                .executes(context -> {
                                            toggleFriend(ToggleFriendEnum.fromString(getString(context, "action"), true), getString(context, "player"));
                                            return 0;
                                        }
                                )
                        )
                )
        );
    }

    private static void toggleFriend(ToggleFriendEnum action, String player) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        List<String> friendList = config.friend.list;
        List<String> enemyList = config.enemy.list;
        switch (action) {
            case FRIEND -> {
                enemyList.remove(player);
                if (friendList.contains(player)) {
                    break;
                }
                friendList.add(player);
            }
            case NEUTRAL -> {
                enemyList.remove(player);
                friendList.remove(player);
            }
            case ENEMY -> {
                friendList.remove(player);
                if (enemyList.contains(player)) {
                    break;
                }
                enemyList.add(player);
            }
        }
        AutoConfig.getConfigHolder(ModConfig.class).setConfig(config);
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }
}
