package io.github.pingisfun.hitboxplus.data;

import io.github.pingisfun.hitboxplus.config.HitBoxPlusConfig;
import io.github.pingisfun.hitboxplus.runtime.RuntimeHitboxLookup;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EntityGroupDataTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test
    void classifiesEveryNonPlayerEntityType() {
        List<EntityType<?>> missing = new ArrayList<>(Registries.ENTITY_TYPE.stream().toList());
        missing.remove(EntityType.PLAYER);
        missing.removeAll(EntityGroupData.ENTITY_GROUPS.keySet());

        assertEquals(List.of(), missing);
    }

    @Test
    void compiledLookupContainsEveryEntityType() {
        HitBoxPlusConfig config = new HitBoxPlusConfig();
        config.normalize();
        RuntimeHitboxLookup lookup = RuntimeHitboxLookup.compile(config);

        Registries.ENTITY_TYPE.forEach(entityType -> assertNotNull(lookup.forEntityType(entityType)));
    }
}
