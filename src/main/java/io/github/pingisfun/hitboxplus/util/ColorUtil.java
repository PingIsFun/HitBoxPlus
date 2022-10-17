package io.github.pingisfun.hitboxplus.util;

import io.github.pingisfun.hitboxplus.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;

import java.awt.*;

public class ColorUtil {
    public static Color getEntityColor(Entity entity) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (entity instanceof OtherClientPlayerEntity) {
            return ColorUtil.decode(config.players.neutral_color);
        }

        return Color.blue;
    }

    private static Color decode(int hex) {
        return Color.decode(String.valueOf(hex));
    }
}
