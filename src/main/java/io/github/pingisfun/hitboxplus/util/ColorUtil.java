package io.github.pingisfun.hitboxplus.util;

import io.github.pingisfun.hitboxplus.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;

import java.awt.*;

public class ColorUtil {
    public static Color getEntityColor(Entity entity) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        if (entity instanceof PlayerEntity && config.player.isEnabled) {
            if (entity instanceof ClientPlayerEntity) {
                return ColorUtil.decode(config.player.self.color, config.player.self.alpha);
            } else if (entity instanceof OtherClientPlayerEntity) {
                String username = entity.getName().getString();
                if (config.player.friend.list.contains((username))) {
                    return ColorUtil.decode(config.player.friend.color, config.player.friend.alpha);
                } else if (config.player.enemy.list.contains((username))) {
                    return ColorUtil.decode(config.player.enemy.color, config.player.enemy.alpha);
                } else {
                    return ColorUtil.decode(config.player.neutral.color, config.player.neutral.alpha);
                }
            }
        } else if (entity instanceof HostileEntity && config.entity.hostile.isEnabled) {
            return ColorUtil.decode(config.entity.hostile.color, config.entity.hostile.alpha);
        } else if ((entity instanceof PassiveEntity || entity instanceof AllayEntity || entity instanceof BatEntity) && config.entity.passive.isEnabled) {
            return ColorUtil.decode(config.entity.passive.color, config.entity.passive.alpha);
        } else if ((entity instanceof ProjectileEntity) && config.entity.projectile.isEnabled) {
            return ColorUtil.decode(config.entity.projectile.color, config.entity.projectile.alpha);
        } else if ((entity instanceof AbstractDecorationEntity || entity instanceof ArmorStandEntity) && config.entity.decoration.isEnabled) {
            return ColorUtil.decode(config.entity.decoration.color, config.entity.decoration.alpha);
        } else if ((entity instanceof AbstractMinecartEntity || entity instanceof BoatEntity) && config.entity.vehicle.isEnabled) {
            return ColorUtil.decode(config.entity.vehicle.color, config.entity.vehicle.alpha);
        } else if (isMiscEntity(entity) && config.entity.misc.isEnabled) {
            if (entity instanceof AreaEffectCloudEntity) {
                return ColorUtil.decode(config.entity.misc.areaEffectCloud.color, config.entity.misc.areaEffectCloud.alpha);
            } else if (entity instanceof ExperienceOrbEntity) {
                return ColorUtil.decode(config.entity.misc.experienceOrb.color, config.entity.misc.experienceOrb.alpha);
            } else if (entity instanceof EyeOfEnderEntity) {
                return ColorUtil.decode(config.entity.misc.eyeOfEnder.color, config.entity.misc.eyeOfEnder.alpha);
            } else if (entity instanceof FallingBlockEntity) {
                return ColorUtil.decode(config.entity.misc.fallingBlock.color, config.entity.misc.fallingBlock.alpha);
            } else if (entity instanceof ItemEntity) {
                return ColorUtil.decode(config.entity.misc.item.color, config.entity.misc.item.alpha);
            } else if (entity instanceof TntEntity) {
                return ColorUtil.decode(config.entity.misc.tnt.color, config.entity.misc.tnt.alpha);
            } else if (entity instanceof EndCrystalEntity) {
                return ColorUtil.decode(config.entity.misc.endCrystalEntity.color, config.entity.misc.endCrystalEntity.alpha);
            }

        }


        return ColorUtil.decode(config.color, config.alpha);
    }

    private static Color decode(int hex, int transparency) {
        int alpha = ((100 - (transparency * 10)) * 25) / 10;

        if (alpha == 0) {
            alpha = 1;
        }

        Color rgb = Color.decode(String.valueOf(hex));
        return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), alpha);

    }

    private static boolean isMiscEntity(Entity entity) {
        return entity instanceof AreaEffectCloudEntity || entity instanceof ExperienceOrbEntity || entity instanceof EyeOfEnderEntity || entity instanceof FallingBlockEntity || entity instanceof ItemEntity || entity instanceof TntEntity;

    }
}
