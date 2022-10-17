package io.github.pingisfun.hitboxplus.util;

import io.github.pingisfun.hitboxplus.HitboxPlus;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

import java.awt.*;

public class BoxRenderUtil {
    public static void drawBox(MatrixStack matrices, VertexConsumer vertices, Entity entity) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());


        Color hitbox_color = ColorUtil.getEntityColor(entity);
        HitboxPlus.LOGGER.info(String.valueOf(hitbox_color));
        HitboxPlus.LOGGER.info(String.valueOf(entity));
        float red = 256 - hitbox_color.getRed();
        float green = 256 - hitbox_color.getGreen();
        float blue = 256 - hitbox_color.getBlue();
        float alpha = hitbox_color.getTransparency();
        HitboxPlus.LOGGER.info(String.valueOf(alpha));

        WorldRenderer.drawBox(matrices, vertices, box, red, green, blue, 1.0f);
    }
}
