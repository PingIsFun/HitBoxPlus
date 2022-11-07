package io.github.pingisfun.hitboxplus;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
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
	}
}
