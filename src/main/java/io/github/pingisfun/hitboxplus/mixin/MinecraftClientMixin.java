package io.github.pingisfun.hitboxplus.mixin;

import io.github.pingisfun.hitboxplus.config.PlayerRelation;
import io.github.pingisfun.hitboxplus.runtime.PlayerRelationController;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Shadow
    public net.minecraft.world.phys.HitResult hitResult;

    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void hitboxplus$cyclePlayerRelation(CallbackInfo ci) {
        if (!(hitResult instanceof EntityHitResult entityHitResult)) {
            return;
        }

        if (!(entityHitResult.getEntity() instanceof Player player)) {
            return;
        }

        PlayerRelation relation = PlayerRelationController.cycle(player);
        PlayerRelationController.announce(player.getName().getString(), relation);
        ci.cancel();
    }
}
