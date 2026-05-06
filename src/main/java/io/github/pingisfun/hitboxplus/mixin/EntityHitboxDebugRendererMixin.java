//? if >=1.21.11 {
package io.github.pingisfun.hitboxplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.pingisfun.hitboxplus.config.HitboxPattern;
import io.github.pingisfun.hitboxplus.runtime.ResolvedHitboxStyle;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxLookup;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.VisibilityConfigurable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
    @WrapOperation(
            method = "drawHitbox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/debug/gizmo/GizmoDrawing;box(Lnet/minecraft/util/math/Box;Lnet/minecraft/client/render/DrawStyle;)Lnet/minecraft/world/debug/gizmo/VisibilityConfigurable;",
                    ordinal = 0
            )
    )
    private VisibilityConfigurable hitboxplus$drawPrimaryHitbox(
            Box box,
            DrawStyle drawStyle,
            Operation<VisibilityConfigurable> original,
            Entity entity,
            float tickProgress,
            boolean inLocalServer
    ) {
        RuntimeHitboxLookup lookup = RuntimeHitboxState.lookup();
        if (inLocalServer || !lookup.isEnabled()) {
            return original.call(box, drawStyle);
        }

        ResolvedHitboxStyle style = resolveStyle(lookup, entity);
        if (!style.showHitbox()) {
            return null;
        }

        if (style.hitboxPattern() == HitboxPattern.FULL) {
            return original.call(box, DrawStyle.stroked(style.opaqueArgb(), style.hitboxThickness()));
        }

        drawPatternedBox(box, style);
        return null;
    }

    @WrapOperation(
            method = "drawHitbox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/debug/gizmo/GizmoDrawing;box(Lnet/minecraft/util/math/Box;Lnet/minecraft/client/render/DrawStyle;)Lnet/minecraft/world/debug/gizmo/VisibilityConfigurable;",
                    ordinal = 2
            )
    )
    private VisibilityConfigurable hitboxplus$drawEyeLine(
            Box box,
            DrawStyle drawStyle,
            Operation<VisibilityConfigurable> original,
            Entity entity,
            float tickProgress,
            boolean inLocalServer
    ) {
        RuntimeHitboxLookup lookup = RuntimeHitboxState.lookup();
        if (inLocalServer || !lookup.isEnabled()) {
            return original.call(box, drawStyle);
        }

        ResolvedHitboxStyle style = resolveStyle(lookup, entity);
        if (!style.showEyeLine()) {
            return null;
        }
        return original.call(box, drawStyle);
    }

    @WrapOperation(
            method = "drawHitbox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/debug/gizmo/GizmoDrawing;arrow(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;I)Lnet/minecraft/world/debug/gizmo/VisibilityConfigurable;",
                    ordinal = 0
            )
    )
    private VisibilityConfigurable hitboxplus$drawLookDirection(
            Vec3d start,
            Vec3d end,
            int color,
            Operation<VisibilityConfigurable> original,
            Entity entity,
            float tickProgress,
            boolean inLocalServer
    ) {
        RuntimeHitboxLookup lookup = RuntimeHitboxState.lookup();
        if (inLocalServer || !lookup.isEnabled()) {
            return original.call(start, end, color);
        }

        ResolvedHitboxStyle style = resolveStyle(lookup, entity);
        if (!style.showLookDirection()) {
            return null;
        }
        return original.call(start, end, color);
    }

    private static void drawPatternedBox(Box box, ResolvedHitboxStyle style) {
        Vec3d minMinMin = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d minMinMax = new Vec3d(box.minX, box.minY, box.maxZ);
        Vec3d minMaxMin = new Vec3d(box.minX, box.maxY, box.minZ);
        Vec3d minMaxMax = new Vec3d(box.minX, box.maxY, box.maxZ);
        Vec3d maxMinMin = new Vec3d(box.maxX, box.minY, box.minZ);
        Vec3d maxMinMax = new Vec3d(box.maxX, box.minY, box.maxZ);
        Vec3d maxMaxMin = new Vec3d(box.maxX, box.maxY, box.minZ);
        Vec3d maxMaxMax = new Vec3d(box.maxX, box.maxY, box.maxZ);

        drawPatternedEdge(minMinMin, maxMinMin, style);
        drawPatternedEdge(minMinMax, maxMinMax, style);
        drawPatternedEdge(minMaxMin, maxMaxMin, style);
        drawPatternedEdge(minMaxMax, maxMaxMax, style);
        drawPatternedEdge(minMinMin, minMinMax, style);
        drawPatternedEdge(maxMinMin, maxMinMax, style);
        drawPatternedEdge(minMaxMin, minMaxMax, style);
        drawPatternedEdge(maxMaxMin, maxMaxMax, style);
        drawPatternedEdge(minMinMin, minMaxMin, style);
        drawPatternedEdge(maxMinMin, maxMaxMin, style);
        drawPatternedEdge(minMinMax, minMaxMax, style);
        drawPatternedEdge(maxMinMax, maxMaxMax, style);
    }

    private static void drawPatternedEdge(Vec3d start, Vec3d end, ResolvedHitboxStyle style) {
        double length = start.distanceTo(end);
        if (length <= 0.0D) {
            return;
        }

        double dashLength = 0.07D;
        double gapLength = 0.10D;
        for (double distance = 0.0D; distance < length; distance += dashLength + gapLength) {
            double segmentEnd = Math.min(distance + dashLength, length);
            if (segmentEnd <= distance) {
                continue;
            }
            GizmoDrawing.line(
                    start.lerp(end, distance / length),
                    start.lerp(end, segmentEnd / length),
                    style.opaqueArgb(),
                    style.hitboxThickness()
            );
        }
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
//?}
