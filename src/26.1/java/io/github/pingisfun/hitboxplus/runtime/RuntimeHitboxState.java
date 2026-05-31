package io.github.pingisfun.hitboxplus.runtime;

import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfig;

import java.util.concurrent.atomic.AtomicReference;

public final class RuntimeHitboxState {
    private static final AtomicReference<RuntimeHitboxLookup> LOOKUP = new AtomicReference<>(
            RuntimeHitboxLookup.compile(new HitBoxPlusConfig())
    );

    private RuntimeHitboxState() {
    }

    public static RuntimeHitboxLookup lookup() {
        return LOOKUP.get();
    }

    public static void rebuild(HitBoxPlusConfig config) {
        LOOKUP.set(RuntimeHitboxLookup.compile(config));
    }
}
