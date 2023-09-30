package io.github.pingisfun.hitboxplus;

import io.github.pingisfun.hitboxplus.commands.Register;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HitboxPlus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "hitboxplus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

//	For dev debug comment when committing
//	public static void INFO(Object... obj) {
//		LOGGER.info("<-----------------");
//		for (Object i : obj) {
//			LOGGER.info(String.valueOf(i));
//		}
//
//		LOGGER.info("----------------->");
//	}
//	public static void SINFO(Object... obj) {
//		for (Object i : obj) {
//			LOGGER.info(String.valueOf(i));
//		}
//	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

		KeyBinding keyBinding = new KeyBinding("Open Config", InputUtil.GLFW_KEY_B, "HitBox+");
		KeyBindingHelper.registerKeyBinding(keyBinding);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (keyBinding.isPressed()) {
				Screen configScreen = AutoConfig.getConfigScreen(ModConfig.class, client.currentScreen).get();
				client.setScreen(configScreen);
			}
		});
		ClientCommandRegistrationCallback.EVENT.register(Register::registerCommands);
	}

}
