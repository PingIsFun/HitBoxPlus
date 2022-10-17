package io.github.pingisfun.hitboxplus.util;

import io.github.pingisfun.hitboxplus.HitboxPlus;
import io.github.pingisfun.hitboxplus.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.awt.*;

public class ColorUtil {
    public static Color getEntityColor(Entity entity) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        if (entity instanceof PlayerEntity) {
            if (entity instanceof ClientPlayerEntity) {
                return ColorUtil.decode(config.players.self_color, config.players.self_alpha);
            }

            else if (entity instanceof OtherClientPlayerEntity) {
                return ColorUtil.decode(config.players.neutral_color, config.players.neutral_alpha);
            }
        }

        return Color.blue;
    }

    private static Color decode(int hex, int transparency) {
        int alpha = ((100 - (transparency * 10)) * 25) / 10;

        if (alpha == 0) {
            alpha = 1;
        }

        Color rgb = Color.decode(String.valueOf(hex));
        return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), alpha);

    }
}
