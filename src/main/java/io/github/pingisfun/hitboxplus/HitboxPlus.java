package io.github.pingisfun.hitboxplus;

import io.github.pingisfun.hitboxplus.commands.ModifyPlayerListConfigSuggestionProvider;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class HitboxPlus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "hitboxplus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	//	For dev debug comment when committing
	public static void INFO(Object obj) {
		LOGGER.info(String.valueOf(obj));
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
//			dispatcher.register(ClientCommandManager.literal("test").executes(context -> {
//				ArrayList<String> players = new ArrayList<>();
//				assert MinecraftClient.getInstance().world != null;
//				MinecraftClient.getInstance().world.getPlayers().forEach(player -> {
//					if (player instanceof OtherClientPlayerEntity) {
//						players.add(player.getEntityName());
//					}
//				});
//
//				INFO(players);
////				context.getSource().sendFeedback(Text.literal(String.valueOf(player)));
//
//				return 0;
//			}));

			// Command with argument
			dispatcher.register(literal("foo")
					.then(literal("bar")
							.executes(context -> {
								INFO("HELLO");
								return 1;
							}).then(literal("foobar")
									.executes(context -> {
										INFO("HELLO2");
										return 1;
									})
							)
					)
			);

			dispatcher.register(
					ClientCommandManager.literal("test")

							.then(ClientCommandManager.argument("username", EntityArgumentType.player()).suggests(new ModifyPlayerListConfigSuggestionProvider().getSuggestions()))
							.suggests
			);


		});
	}
}
//							.then(ClientCommandManager.argument("username", EntityArgumentType.player()).executes(context -> {return 0 })
