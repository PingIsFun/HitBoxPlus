package io.github.pingisfun.hitboxplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.pingisfun.hitboxplus.runtime.ResolvedHitboxStyle;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxLookup;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
    @WrapOperation(
            method = "drawHitbox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/DrawStyle;stroked(I)Lnet/minecraft/client/render/DrawStyle;",
                    ordinal = 0
            )
    )
    private DrawStyle hitboxplus$replacePrimaryHitboxColor(
            int color,
            Operation<DrawStyle> original,
            Entity entity,
            float tickProgress,
            boolean inLocalServer
    ) {
        RuntimeHitboxLookup lookup = RuntimeHitboxState.lookup();
        if (inLocalServer || !lookup.isEnabled()) {
            return original.call(color);
        }

        ResolvedHitboxStyle style = resolveStyle(lookup, entity);
        return original.call(style.opaqueArgb());
    }

    private static ResolvedHitboxStyle resolveStyle(RuntimeHitboxLookup lookup, Entity entity) {
        if (entity instanceof ClientPlayerEntity || entity == MinecraftClient.getInstance().player) {
            return lookup.selfPlayerStyle();
        }

        if (entity instanceof PlayerEntity) {
            return lookup.forPlayerName(entity.getName().getString());
        }

        return lookup.forEntityType(entity.getType());
    }
}
