package io.github.pingisfun.hitboxplus.util;

import io.github.pingisfun.hitboxplus.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class BoxRenderUtil {
    public static void drawBox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

        if (entity instanceof EnderDragonEntity) {
            Color dragon_part_color = ColorUtil.getDragonPartColor();
            float red = 256 - dragon_part_color.getRed();
            float green = 256 - dragon_part_color.getGreen();
            float blue = 256 - dragon_part_color.getBlue();
            float alpha = dragon_part_color.getAlpha();

            if (alpha != 250.0) {
                double d = -MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
                double e = -MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
                double f = -MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
                for (EnderDragonPart enderDragonPart : ((EnderDragonEntity) entity).getBodyParts()) {
                    matrices.push();
                    double g = d + MathHelper.lerp(tickDelta, enderDragonPart.lastRenderX, enderDragonPart.getX());
                    double h = e + MathHelper.lerp(tickDelta, enderDragonPart.lastRenderY, enderDragonPart.getY());
                    double i = f + MathHelper.lerp(tickDelta, enderDragonPart.lastRenderZ, enderDragonPart.getZ());
                    matrices.translate(g, h, i);
                    WorldRenderer.drawBox(matrices, vertices, enderDragonPart.getBoundingBox().offset(-enderDragonPart.getX(), -enderDragonPart.getY(), -enderDragonPart.getZ()), red, green, blue, alpha);
                    matrices.pop();
                }
            }
            if (!config.ender_dragon.boxHitbox && config.ender_dragon.isEnabled) {
                return;
            }

        }


        Color hitbox_color = ColorUtil.getEntityColor(entity);
        float red = 256 - hitbox_color.getRed();
        float green = 256 - hitbox_color.getGreen();
        float blue = 256 - hitbox_color.getBlue();
        float alpha = hitbox_color.getAlpha();

        if (alpha == 250.0) {
            // Prevent weird invisible hitboixes
            return;
        }

        WorldRenderer.drawBox(matrices, vertices, box, red, green, blue, alpha);
    }
}
