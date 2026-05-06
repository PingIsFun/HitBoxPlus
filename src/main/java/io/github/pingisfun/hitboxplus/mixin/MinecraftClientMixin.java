package io.github.pingisfun.hitboxplus.mixin;

import io.github.pingisfun.hitboxplus.config.PlayerRelation;
import io.github.pingisfun.hitboxplus.runtime.PlayerRelationController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    public net.minecraft.util.hit.HitResult crosshairTarget;

    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    private void hitboxplus$cyclePlayerRelation(CallbackInfo ci) {
        if (!(crosshairTarget instanceof EntityHitResult entityHitResult)) {
            return;
        }

        if (!(entityHitResult.getEntity() instanceof PlayerEntity player)) {
            return;
        }

        PlayerRelation relation = PlayerRelationController.cycle(player);
        PlayerRelationController.announce(player.getName().getString(), relation);
        ci.cancel();
    }
}
