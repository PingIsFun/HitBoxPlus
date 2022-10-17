package io.github.pingisfun.hitboxplus.mixin;

import io.github.pingisfun.hitboxplus.util.BoxRenderUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class RenderHitboxMixin {
	@Inject(at = @At("HEAD"), method = "renderHitbox", cancellable = true)
	private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
		BoxRenderUtil.drawBox(matrices, vertices, entity);
		ci.cancel();
	}
}
