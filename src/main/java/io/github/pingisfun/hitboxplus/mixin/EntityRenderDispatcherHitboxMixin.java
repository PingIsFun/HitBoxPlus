//? if <1.21.9 {
/*package io.github.pingisfun.hitboxplus.mixin;

import io.github.pingisfun.hitboxplus.config.HitboxPattern;
import io.github.pingisfun.hitboxplus.runtime.ResolvedHitboxStyle;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxLookup;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherHitboxMixin {
    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void hitboxplus$renderLegacyHitbox(
            MatrixStack matrices,
            VertexConsumer vertices,
            Entity entity,
            float tickDelta,
            float red,
            float green,
            float blue,
            CallbackInfo ci
    ) {
        RuntimeHitboxLookup lookup = RuntimeHitboxState.lookup();
        if (!lookup.isEnabled()) {
            return;
        }

        ResolvedHitboxStyle style = resolveStyle(lookup, entity);
        if (style.showHitbox()) {
            Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
            if (style.hitboxPattern() == HitboxPattern.FULL) {
                drawFullBox(matrices, vertices, box, style);
            } else {
                drawPatternedBox(matrices, vertices, box, style);
            }
        }

        if (style.showEyeLine()) {
            double eyeHeight = entity.getStandingEyeHeight();
            drawPlainBox(
                    matrices,
                    vertices,
                    new Box(
                            entity.getBoundingBox().minX - entity.getX(),
                            eyeHeight - 0.01D,
                            entity.getBoundingBox().minZ - entity.getZ(),
                            entity.getBoundingBox().maxX - entity.getX(),
                            eyeHeight + 0.01D,
                            entity.getBoundingBox().maxZ - entity.getZ()
                    ),
                    0xFFFF0000
            );
        }

        if (style.showLookDirection()) {
            drawLine(
                    matrices,
                    vertices,
                    new Vector3f(0.0F, entity.getStandingEyeHeight(), 0.0F),
                    entity.getRotationVec(tickDelta).multiply(2.0D),
                    0xFF0000FF
            );
        }

        ci.cancel();
    }

    private static void drawFullBox(MatrixStack matrices, VertexConsumer vertices, Box box, ResolvedHitboxStyle style) {
        drawPlainBox(matrices, vertices, box, style.opaqueArgb(), style);
    }

    private static void drawPlainBox(MatrixStack matrices, VertexConsumer vertices, Box box, int argb) {
        drawPlainBox(matrices, vertices, box, argb, null);
    }

    private static void drawPlainBox(MatrixStack matrices, VertexConsumer vertices, Box box, int argb, ResolvedHitboxStyle style) {
        Vec3d minMinMin = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d minMinMax = new Vec3d(box.minX, box.minY, box.maxZ);
        Vec3d minMaxMin = new Vec3d(box.minX, box.maxY, box.minZ);
        Vec3d minMaxMax = new Vec3d(box.minX, box.maxY, box.maxZ);
        Vec3d maxMinMin = new Vec3d(box.maxX, box.minY, box.minZ);
        Vec3d maxMinMax = new Vec3d(box.maxX, box.minY, box.maxZ);
        Vec3d maxMaxMin = new Vec3d(box.maxX, box.maxY, box.minZ);
        Vec3d maxMaxMax = new Vec3d(box.maxX, box.maxY, box.maxZ);

        drawLine(matrices, vertices, minMinMin, maxMinMin, argb, style);
        drawLine(matrices, vertices, minMinMax, maxMinMax, argb, style);
        drawLine(matrices, vertices, minMaxMin, maxMaxMin, argb, style);
        drawLine(matrices, vertices, minMaxMax, maxMaxMax, argb, style);
        drawLine(matrices, vertices, minMinMin, minMinMax, argb, style);
        drawLine(matrices, vertices, maxMinMin, maxMinMax, argb, style);
        drawLine(matrices, vertices, minMaxMin, minMaxMax, argb, style);
        drawLine(matrices, vertices, maxMaxMin, maxMaxMax, argb, style);
        drawLine(matrices, vertices, minMinMin, minMaxMin, argb, style);
        drawLine(matrices, vertices, maxMinMin, maxMaxMin, argb, style);
        drawLine(matrices, vertices, minMinMax, minMaxMax, argb, style);
        drawLine(matrices, vertices, maxMinMax, maxMaxMax, argb, style);
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
        drawLine(matrices, vertices, start, end, style.opaqueArgb(), style);
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer vertices, Vec3d start, Vec3d end, int argb, ResolvedHitboxStyle style) {
        int passes = style == null ? 1 : Math.max(1, Math.min(6, Math.round(style.hitboxThickness())));
        double spacing = 0.002D;
        for (int pass = 0; pass < passes; pass++) {
            Vec3d offset = lineOffset(start, end, pass, spacing);
            drawLine(
                    matrices,
                    vertices,
                    new Vector3f((float) (start.x + offset.x), (float) (start.y + offset.y), (float) (start.z + offset.z)),
                    end.subtract(start),
                    argb
            );
        }
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer vertices, Vector3f start, Vec3d vector, int argb) {
        MatrixStack.Entry entry = matrices.peek();
        vertices.vertex(entry, start).color(argb).normal(entry, (float) vector.x, (float) vector.y, (float) vector.z);
        vertices.vertex(entry, (float) (start.x() + vector.x), (float) (start.y() + vector.y), (float) (start.z() + vector.z))
                .color(argb)
                .normal(entry, (float) vector.x, (float) vector.y, (float) vector.z);
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
