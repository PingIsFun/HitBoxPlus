//? if >=1.21.9 && <1.21.11 {
/*package io.github.pingisfun.hitboxplus.mixin;

import com.google.common.collect.ImmutableList;
import io.github.pingisfun.hitboxplus.runtime.DebugHitboxRenderOverrides;
import io.github.pingisfun.hitboxplus.runtime.ResolvedHitboxStyle;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxLookup;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderManager.class)
public class EntityRenderManagerHitboxStateMixin {
    @Inject(method = "getAndUpdateRenderState", at = @At("RETURN"))
    private <E extends Entity> void hitboxplus$customizeHitboxState(E entity, float tickProgress, CallbackInfoReturnable<EntityRenderState> cir) {
        RuntimeHitboxLookup lookup = RuntimeHitboxState.lookup();
        EntityRenderState state = cir.getReturnValue();
        if (!lookup.isEnabled() || state.hitbox == null) {
            return;
        }

        ResolvedHitboxStyle style = resolveStyle(lookup, entity);
        EntityHitboxAndView hitboxAndView = state.hitbox;
        ImmutableList.Builder<EntityHitbox> hitboxes = ImmutableList.builder();
        ImmutableList<EntityHitbox> originalHitboxes = hitboxAndView.hitboxes();
        for (int index = 0; index < originalHitboxes.size(); index++) {
            EntityHitbox hitbox = originalHitboxes.get(index);
            boolean primary = index == 0;
            if (primary && !style.showHitbox()) {
                continue;
            }
            if (!primary && !style.showEyeLine()) {
                continue;
            }

            EntityHitbox replacement = primary ? recolor(hitbox, style) : hitbox;
            hitboxes.add(replacement);
            DebugHitboxRenderOverrides.register(replacement, style, primary);
        }

        double viewX = style.showLookDirection() ? hitboxAndView.viewX() : 0.0D;
        double viewY = style.showLookDirection() ? hitboxAndView.viewY() : 0.0D;
        double viewZ = style.showLookDirection() ? hitboxAndView.viewZ() : 0.0D;
        state.hitbox = new EntityHitboxAndView(viewX, viewY, viewZ, hitboxes.build());
    }

    private static EntityHitbox recolor(EntityHitbox hitbox, ResolvedHitboxStyle style) {
        return new EntityHitbox(
                hitbox.x0(),
                hitbox.y0(),
                hitbox.z0(),
                hitbox.x1(),
                hitbox.y1(),
                hitbox.z1(),
                hitbox.offsetX(),
                hitbox.offsetY(),
                hitbox.offsetZ(),
                ((style.opaqueArgb() >> 16) & 0xFF) / 255.0F,
                ((style.opaqueArgb() >> 8) & 0xFF) / 255.0F,
                (style.opaqueArgb() & 0xFF) / 255.0F
        );
    }

    private static ResolvedHitboxStyle resolveStyle(RuntimeHitboxLookup lookup, Entity entity) {
        if (entity instanceof ClientPlayerEntity || entity == MinecraftClient.getInstance().player) {
            return lookup.selfPlayerStyle();
        }

        if (entity instanceof PlayerEntity) {
            return lookup.forPlayer((PlayerEntity) entity);
        }

        return lookup.forEntityType(entity.getType());
    }
}
*///?}
