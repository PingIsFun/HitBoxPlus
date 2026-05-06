//? if >=1.21.9 && <1.21.11 {
/*package io.github.pingisfun.hitboxplus.mixin;

import io.github.pingisfun.hitboxplus.config.HitboxPattern;
import io.github.pingisfun.hitboxplus.runtime.DebugHitboxRenderOverrides;
import io.github.pingisfun.hitboxplus.runtime.ResolvedHitboxStyle;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.command.DebugHitboxCommandRenderer;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHitboxCommandRenderer.class)
public class DebugHitboxCommandRendererMixin {
    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void hitboxplus$renderHitbox(MatrixStack matrices, VertexConsumer vertices, EntityHitbox hitbox, CallbackInfo ci) {
        DebugHitboxRenderOverrides.Override override = DebugHitboxRenderOverrides.get(hitbox);
        if (override == null || !override.primary()) {
            return;
        }

        ResolvedHitboxStyle style = override.style();
        if (!style.showHitbox()) {
            ci.cancel();
            return;
        }

        matrices.push();
        matrices.translate(hitbox.offsetX(), hitbox.offsetY(), hitbox.offsetZ());
        Box box = new Box(hitbox.x0(), hitbox.y0(), hitbox.z0(), hitbox.x1(), hitbox.y1(), hitbox.z1());
        if (style.hitboxPattern() == HitboxPattern.FULL) {
            drawFullBox(matrices, vertices, box, style);
        } else {
            drawPatternedBox(matrices, vertices, box, style);
        }
        matrices.pop();
        ci.cancel();
    }

    private static void drawFullBox(MatrixStack matrices, VertexConsumer vertices, Box box, ResolvedHitboxStyle style) {
        float red = ((style.opaqueArgb() >> 16) & 0xFF) / 255.0F;
        float green = ((style.opaqueArgb() >> 8) & 0xFF) / 255.0F;
        float blue = (style.opaqueArgb() & 0xFF) / 255.0F;
        double expansion = thicknessExpansion(style);
        int passes = Math.max(1, Math.min(6, Math.round(style.hitboxThickness())));
        for (int pass = 0; pass < passes; pass++) {
            VertexRendering.drawBox(matrices.peek(), vertices, box.expand(expansion * pass), red, green, blue, 1.0F);
        }
    }

    private static void drawPatternedBox(MatrixStack matrices, VertexConsumer vertices, Box box, ResolvedHitboxStyle style) {
        Vec3d minMinMin = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d minMinMax = new Vec3d(box.minX, box.minY, box.maxZ);
        Vec3d minMaxMin = new Vec3d(box.minX, box.maxY, box.minZ);
        Vec3d minMaxMax = new Vec3d(box.minX, box.maxY, box.maxZ);
        Vec3d maxMinMin = new Vec3d(box.maxX, box.minY, box.minZ);
        Vec3d maxMinMax = new Vec3d(box.maxX, box.minY, box.maxZ);
        Vec3d maxMaxMin = new Vec3d(box.maxX, box.maxY, box.minZ);
        Vec3d maxMaxMax = new Vec3d(box.maxX, box.maxY, box.maxZ);

        drawPatternedEdge(matrices, vertices, minMinMin, maxMinMin, style);
        drawPatternedEdge(matrices, vertices, minMinMax, maxMinMax, style);
        drawPatternedEdge(matrices, vertices, minMaxMin, maxMaxMin, style);
        drawPatternedEdge(matrices, vertices, minMaxMax, maxMaxMax, style);
        drawPatternedEdge(matrices, vertices, minMinMin, minMinMax, style);
        drawPatternedEdge(matrices, vertices, maxMinMin, maxMinMax, style);
        drawPatternedEdge(matrices, vertices, minMaxMin, minMaxMax, style);
        drawPatternedEdge(matrices, vertices, maxMaxMin, maxMaxMax, style);
        drawPatternedEdge(matrices, vertices, minMinMin, minMaxMin, style);
        drawPatternedEdge(matrices, vertices, maxMinMin, maxMaxMin, style);
        drawPatternedEdge(matrices, vertices, minMinMax, minMaxMax, style);
        drawPatternedEdge(matrices, vertices, maxMinMax, maxMaxMax, style);
    }

    private static void drawPatternedEdge(MatrixStack matrices, VertexConsumer vertices, Vec3d start, Vec3d end, ResolvedHitboxStyle style) {
        double length = start.distanceTo(end);
        if (length <= 0.0D) {
            return;
        }

        double dashLength = 0.07D;
        double gapLength = 0.10D;
        float red = ((style.opaqueArgb() >> 16) & 0xFF) / 255.0F;
        float green = ((style.opaqueArgb() >> 8) & 0xFF) / 255.0F;
        float blue = (style.opaqueArgb() & 0xFF) / 255.0F;
        double expansion = thicknessExpansion(style);
        int passes = Math.max(1, Math.min(6, Math.round(style.hitboxThickness())));

        for (double distance = 0.0D; distance < length; distance += dashLength + gapLength) {
            double segmentEnd = Math.min(distance + dashLength, length);
            if (segmentEnd <= distance) {
                continue;
            }

            Vec3d segmentStart = start.lerp(end, distance / length);
            Vec3d segmentFinish = start.lerp(end, segmentEnd / length);
            for (int pass = 0; pass < passes; pass++) {
                double offset = expansion * pass;
                VertexRendering.drawBox(
                        matrices.peek(),
                        vertices,
                        Math.min(segmentStart.x, segmentFinish.x) - offset,
                        Math.min(segmentStart.y, segmentFinish.y) - offset,
                        Math.min(segmentStart.z, segmentFinish.z) - offset,
                        Math.max(segmentStart.x, segmentFinish.x) + offset,
                        Math.max(segmentStart.y, segmentFinish.y) + offset,
                        Math.max(segmentStart.z, segmentFinish.z) + offset,
                        red,
                        green,
                        blue,
                        1.0F
                );
            }
        }
    }

    private static double thicknessExpansion(ResolvedHitboxStyle style) {
        return Math.max(0.0D, style.hitboxThickness() - 1.0F) * 0.0015D;
    }

}
*///?}
