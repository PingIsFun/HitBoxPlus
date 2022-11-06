package io.github.pingisfun.hitboxplus.mixin;

import io.github.pingisfun.hitboxplus.ModConfig;
import io.github.pingisfun.hitboxplus.util.ConfEnums;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "doItemPick")
    private void toggleHostility(CallbackInfo ci) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (config.middleClick == ConfEnums.PlayerListTypes.DISABLED) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        if (hit.getType() != HitResult.Type.ENTITY) {
            return;
        }
        EntityHitResult entityHit = (EntityHitResult) hit;

        if (entityHit.getEntity() instanceof OtherClientPlayerEntity) {
            String name = entityHit.getEntity().getEntityName();
            boolean wasEnemy = config.enemy.list.remove(name);
            boolean wasFriend = config.friend.list.remove(name);
            if (config.middleClick == ConfEnums.PlayerListTypes.FRIEND) {
                if (!wasFriend) {
                    config.friend.list.add(name);
                }

            } else if (config.middleClick == ConfEnums.PlayerListTypes.ENEMY) {
                if (!wasEnemy) {
                    config.enemy.list.add(name);
                }
            }

            AutoConfig.getConfigHolder(ModConfig.class).setConfig(config);
            AutoConfig.getConfigHolder(ModConfig.class).save();
        }

    }

}
