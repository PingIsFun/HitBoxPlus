package io.github.pingisfun.hitboxplus.util;

import io.github.pingisfun.hitboxplus.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;

import java.awt.*;

public class ColorUtil {
    public static Color getEntityColor(Entity entity) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        if (entity instanceof PlayerEntity && config.isPlayerConfigEnabled) {
            if (entity instanceof ClientPlayerEntity) {
                return ColorUtil.decode(config.self.color, config.self.alpha);
            } else if (entity instanceof OtherClientPlayerEntity) {
                String username = entity.getName().getString();
                if (config.friend.list.contains((username))) {
                    return ColorUtil.decode(config.friend.color, config.friend.alpha);
                } else if (config.enemy.list.contains((username))) {
                    return ColorUtil.decode(config.enemy.color, config.enemy.alpha);
                } else {
                    return ColorUtil.decode(config.neutral.color, config.neutral.alpha);
                }
            }
        } else if (entity instanceof EnderDragonEntity && config.enderDragon.isEnabled && config.enderDragon.boxHitbox) {
            return ColorUtil.decode(config.enderDragon.color, config.enderDragon.alpha);
        } else if (entity instanceof HostileEntity && config.hostile.isEnabled) {
            return ColorUtil.decode(config.hostile.color, config.hostile.alpha);
        } else if ((entity instanceof PassiveEntity || entity instanceof AmbientEntity) && config.passive.isEnabled) {
            return ColorUtil.decode(config.passive.color, config.passive.alpha);
        } else if ((entity instanceof ProjectileEntity) && config.projectile.isEnabled) {
            if (entity instanceof PersistentProjectileEntity persistentProjectile
                    && !config.projectile.renderStuck
                    && persistentProjectile.pickupType == PersistentProjectileEntity.PickupPermission.DISALLOWED) {
                return ColorUtil.transparent();
            }
            return ColorUtil.decode(config.projectile.color, config.projectile.alpha);
        } else if ((entity instanceof AbstractDecorationEntity || entity instanceof ArmorStandEntity) && config.decoration.isEnabled) {
            return ColorUtil.decode(config.decoration.color, config.decoration.alpha);
        } else if ((entity instanceof AbstractMinecartEntity || entity instanceof BoatEntity) && config.vehicle.isEnabled) {
            return ColorUtil.decode(config.vehicle.color, config.vehicle.alpha);
        } else if (isMiscEntity(entity) && config.misc.isEnabled) {
            if (entity instanceof AreaEffectCloudEntity) {
                return ColorUtil.decode(config.misc.areaEffectCloud.color, config.misc.areaEffectCloud.alpha);
            } else if (entity instanceof ExperienceOrbEntity) {
                return ColorUtil.decode(config.misc.experienceOrb.color, config.misc.experienceOrb.alpha);
            } else if (entity instanceof EyeOfEnderEntity) {
                return ColorUtil.decode(config.misc.eyeOfEnder.color, config.misc.eyeOfEnder.alpha);
            } else if (entity instanceof FallingBlockEntity) {
                return ColorUtil.decode(config.misc.fallingBlock.color, config.misc.fallingBlock.alpha);
            } else if (entity instanceof ItemEntity) {
                return ColorUtil.decode(config.misc.item.color, config.misc.item.alpha);
            } else if (entity instanceof TntEntity) {
                return ColorUtil.decode(config.misc.tnt.color, config.misc.tnt.alpha);
            } else if (entity instanceof EndCrystalEntity) {
                return ColorUtil.decode(config.misc.endCrystalEntity.color, config.misc.endCrystalEntity.alpha);
            }
        }

        return ColorUtil.decode(config.color, config.alpha);
    }

    public static Color getDragonPartColor() {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (!config.enderDragon.isEnabled) {
            return ColorUtil.decode(config.color, config.alpha);
        } else if (!config.enderDragon.realHitbox) {
            return ColorUtil.decode(config.enderDragon.partColor, 0);
        }
        return ColorUtil.decode(config.enderDragon.partColor, config.enderDragon.partAlpha);
    }

    private static Color decode(int hex, int transparency) {
        int alpha = ((100 - (transparency * 10)) * 25) / 10;

        if (alpha == 0) {
            alpha = 1;
        }

        Color rgb = Color.decode(String.valueOf(hex));
        return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), alpha);
    }

    private static Color transparent() {
        return new Color(0, 0, 0, 0);
    }

    private static boolean isMiscEntity(Entity entity) {
        return entity instanceof AreaEffectCloudEntity || entity instanceof ExperienceOrbEntity || entity instanceof EyeOfEnderEntity || entity instanceof FallingBlockEntity || entity instanceof ItemEntity || entity instanceof TntEntity || entity instanceof EndCrystalEntity;

    }
}
