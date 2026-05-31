package io.github.pingisfun.hitboxplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.pingisfun.hitboxplus.config.HitboxPattern;
import io.github.pingisfun.hitboxplus.runtime.ResolvedHitboxStyle;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxLookup;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoProperties;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
    @WrapOperation(
            method = "showHitboxes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/Gizmos;cuboid(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/gizmos/GizmoStyle;)Lnet/minecraft/gizmos/GizmoProperties;",
                    ordinal = 0
            )
    )
    private GizmoProperties hitboxplus$drawPrimaryHitbox(
            AABB box,
            GizmoStyle drawStyle,
            Operation<GizmoProperties> original,
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
            return original.call(box, GizmoStyle.stroke(style.opaqueArgb(), style.hitboxThickness()));
        }

        drawPatternedBox(box, style);
        return null;
    }

    @WrapOperation(
            method = "showHitboxes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/Gizmos;cuboid(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/gizmos/GizmoStyle;)Lnet/minecraft/gizmos/GizmoProperties;",
                    ordinal = 2
            )
    )
    private GizmoProperties hitboxplus$drawEyeLine(
            AABB box,
            GizmoStyle drawStyle,
            Operation<GizmoProperties> original,
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
            method = "showHitboxes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/Gizmos;arrow(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;I)Lnet/minecraft/gizmos/GizmoProperties;",
                    ordinal = 0
            )
    )
    private GizmoProperties hitboxplus$drawLookDirection(
            Vec3 start,
            Vec3 end,
            int color,
            Operation<GizmoProperties> original,
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

    private static void drawPatternedBox(AABB box, ResolvedHitboxStyle style) {
        Vec3 minMinMin = new Vec3(box.minX, box.minY, box.minZ);
        Vec3 minMinMax = new Vec3(box.minX, box.minY, box.maxZ);
        Vec3 minMaxMin = new Vec3(box.minX, box.maxY, box.minZ);
        Vec3 minMaxMax = new Vec3(box.minX, box.maxY, box.maxZ);
        Vec3 maxMinMin = new Vec3(box.maxX, box.minY, box.minZ);
        Vec3 maxMinMax = new Vec3(box.maxX, box.minY, box.maxZ);
        Vec3 maxMaxMin = new Vec3(box.maxX, box.maxY, box.minZ);
        Vec3 maxMaxMax = new Vec3(box.maxX, box.maxY, box.maxZ);

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

    private static void drawPatternedEdge(Vec3 start, Vec3 end, ResolvedHitboxStyle style) {
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
            Gizmos.line(
                    start.lerp(end, distance / length),
                    start.lerp(end, segmentEnd / length),
                    style.opaqueArgb(),
                    style.hitboxThickness()
            );
        }
    }

    private static ResolvedHitboxStyle resolveStyle(RuntimeHitboxLookup lookup, Entity entity) {
        if (entity instanceof LocalPlayer || entity == Minecraft.getInstance().player) {
            return lookup.selfPlayerStyle();
        }

        if (entity instanceof Player) {
            return lookup.forPlayer((Player) entity);
        }

        return lookup.forEntityType(entity.getType());
    }
}
