package io.github.pingisfun.hitboxplus.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModifyPlayerListConfigSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {

    public static final SuggestionProvider<ServerCommandSource> ALL_RECIPES = SuggestionProviders.register(new Identifier("all_recipes"), (context, builder) -> CommandSource.suggestIdentifiers(context.getSource().getRecipeIds(), builder));

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {

        assert MinecraftClient.getInstance().world != null;
        MinecraftClient.getInstance().world.getPlayers().forEach(player -> {
            if (player instanceof OtherClientPlayerEntity) {
                builder.suggest(player.getEntityName());
            }
        });

        return builder.buildFuture();
    }
}
