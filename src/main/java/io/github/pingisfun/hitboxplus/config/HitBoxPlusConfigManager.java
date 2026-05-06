package io.github.pingisfun.hitboxplus.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.github.pingisfun.hitboxplus.HitBoxPlus;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public final class HitBoxPlusConfigManager {
    private static final ConfigClassHandler<HitBoxPlusConfig> HANDLER = ConfigClassHandler.createBuilder(HitBoxPlusConfig.class)
            .id(Identifier.fromNamespaceAndPath(HitBoxPlus.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("hitboxplus.json5"))
                    .setJson5(true)
                    .build())
            .build();

    private HitBoxPlusConfigManager() {
    }

    public static void load() {
        HANDLER.load();
        HANDLER.instance().normalize();
        RuntimeHitboxState.rebuild(HANDLER.instance());
    }

    public static void save() {
        HANDLER.instance().normalize();
        HANDLER.save();
        RuntimeHitboxState.rebuild(HANDLER.instance());
    }

    public static HitBoxPlusConfig config() {
        return HANDLER.instance();
    }

    public static HitBoxPlusConfig defaults() {
        return HANDLER.defaults();
    }
}
