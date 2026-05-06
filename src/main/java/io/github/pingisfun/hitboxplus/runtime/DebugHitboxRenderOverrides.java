//? if >=1.21.9 && <1.21.11 {
/*package io.github.pingisfun.hitboxplus.runtime;

import net.minecraft.client.render.entity.state.EntityHitbox;

import java.util.Map;
import java.util.WeakHashMap;

public final class DebugHitboxRenderOverrides {
    private static final Map<EntityHitbox, Override> OVERRIDES = new WeakHashMap<>();

    private DebugHitboxRenderOverrides() {
    }

    public static void register(EntityHitbox hitbox, ResolvedHitboxStyle style, boolean primary) {
        OVERRIDES.put(hitbox, new Override(style, primary));
    }

    public static Override get(EntityHitbox hitbox) {
        return OVERRIDES.get(hitbox);
    }

    public record Override(ResolvedHitboxStyle style, boolean primary) {
    }
}
*///?}
