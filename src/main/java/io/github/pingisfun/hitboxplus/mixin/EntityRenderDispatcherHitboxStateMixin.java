//? if >=1.21.5 && <1.21.9 {
/*package io.github.pingisfun.hitboxplus.mixin;

import io.github.pingisfun.hitboxplus.config.HitboxPattern;
import io.github.pingisfun.hitboxplus.runtime.DebugHitboxRenderOverrides;
import io.github.pingisfun.hitboxplus.runtime.ResolvedHitboxStyle;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherHitboxStateMixin {
    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void hitboxplus$renderPatternedHitbox(MatrixStack matrices, VertexConsumer vertices, EntityHitbox hitbox, CallbackInfo ci) {
        DebugHitboxRenderOverrides.Override override = DebugHitboxRenderOverrides.get(hitbox);
        if (override == null || !override.primary()) {
            return;
        }

        ResolvedHitboxStyle style = override.style();
        if (style.hitboxPattern() == HitboxPattern.FULL) {
            return;
        }

        matrices.push();
        matrices.translate(hitbox.offsetX(), hitbox.offsetY(), hitbox.offsetZ());
        Box box = new Box(hitbox.x0(), hitbox.y0(), hitbox.z0(), hitbox.x1(), hitbox.y1(), hitbox.z1());
        drawPatternedBox(matrices, vertices, box, style);
        matrices.pop();
        ci.cancel();
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

        double dashLength = 0.18D;
        double gapLength = 0.12D;

        for (double distance = 0.0D; distance < length; distance += dashLength + gapLength) {
            double segmentEnd = Math.min(distance + dashLength, length);
            if (segmentEnd <= distance) {
                continue;
            }

            Vec3d segmentStart = start.lerp(end, distance / length);
            Vec3d segmentFinish = start.lerp(end, segmentEnd / length);
            drawLine(matrices, vertices, segmentStart, segmentFinish, style);
        }
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer vertices, Vec3d start, Vec3d end, ResolvedHitboxStyle style) {
        int passes = Math.max(1, Math.min(6, Math.round(style.hitboxThickness())));
        double spacing = 0.002D;
        for (int pass = 0; pass < passes; pass++) {
            Vec3d offset = lineOffset(start, end, pass, spacing);
            VertexRendering.drawVector(
                    matrices,
                    vertices,
                    new Vector3f((float) (start.x + offset.x), (float) (start.y + offset.y), (float) (start.z + offset.z)),
                    end.subtract(start),
                    style.opaqueArgb()
            );
        }
    }

    private static Vec3d lineOffset(Vec3d start, Vec3d end, int pass, double spacing) {
        if (pass == 0) {
            return Vec3d.ZERO;
        }

        Vec3d direction = end.subtract(start).normalize();
        Vec3d axis = Math.abs(direction.y) < 0.9D ? new Vec3d(0.0D, 1.0D, 0.0D) : new Vec3d(1.0D, 0.0D, 0.0D);
        Vec3d side = direction.crossProduct(axis).normalize();
        Vec3d up = direction.crossProduct(side).normalize();
        int ring = (pass + 1) / 2;
        return (pass % 2 == 1 ? side : up).multiply(ring * spacing);
    }
}
*///?}
