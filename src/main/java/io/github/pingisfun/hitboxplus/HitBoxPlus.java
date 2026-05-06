package io.github.pingisfun.hitboxplus;

import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfigManager;
import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HitBoxPlus implements ClientModInitializer {
    public static final String MOD_ID = "hitboxplus";
    public static final String VERSION = /*$ mod_version*/ "0.1.0";
    public static final String MINECRAFT = /*$ minecraft*/ "1.21.11";
    public static final Logger LOGGER = LoggerFactory.getLogger("HitBoxPlus");

    @Override
    public void onInitializeClient() {
        HitBoxPlusConfigManager.load();
        LOGGER.info("Initializing HitBoxPlus {} for Minecraft {}", VERSION, MINECRAFT);
    }

    public static Screen createConfigScreen(Screen parent) {
        return HitBoxPlusConfigScreen.create(parent, HitBoxPlusConfigManager.config());
    }
}
