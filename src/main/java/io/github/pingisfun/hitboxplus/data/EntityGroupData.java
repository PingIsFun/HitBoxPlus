package io.github.pingisfun.hitboxplus.data;

import io.github.pingisfun.hitboxplus.config.EntityGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EntityGroupData {
    public static final Map<EntityType<?>, EntityGroup> ENTITY_GROUPS = createEntityGroups();

    private EntityGroupData() {
    }

    private static Map<EntityType<?>, EntityGroup> createEntityGroups() {
        Map<EntityType<?>, EntityGroup> groups = new LinkedHashMap<>();
        addAll(groups, EntityGroup.PASSIVE,
                "allay", "armadillo", "axolotl", "bat", "bee", "camel", "copper_golem", "cat", "chicken",
                "cod", "cow", "dolphin", "donkey", "fox", "frog", "glow_squid", "goat", "horse", "llama",
                "mooshroom", "mule", "nautilus", "ocelot", "panda", "parrot", "pig", "polar_bear",
                "pufferfish", "rabbit", "salmon", "sheep", "sniffer", "snow_golem", "squid", "strider",
                "tadpole", "tropical_fish", "turtle", "trader_llama", "villager", "wandering_trader", "wolf",
                "happy_ghast", "iron_golem", "skeleton_horse");
        addAll(groups, EntityGroup.HOSTILE,
                "blaze", "bogged", "breeze", "camel_husk", "cave_spider", "creeper", "drowned", "enderman",
                "endermite", "evoker", "ghast", "giant", "guardian", "hoglin", "husk", "illusioner",
                "phantom", "parched", "piglin", "piglin_brute", "pillager", "shulker", "silverfish",
                "skeleton", "slime", "spider", "stray", "vex", "vindicator", "witch", "wither_skeleton",
                "zoglin", "zombie", "zombie_horse", "zombie_nautilus", "zombie_villager", "zombified_piglin",
                "ravager", "creaking", "magma_cube");
        addAll(groups, EntityGroup.BOSS,
                "ender_dragon", "warden", "wither", "elder_guardian");
        addAll(groups, EntityGroup.PROJECTILE,
                "arrow", "spectral_arrow", "trident", "snowball", "egg", "ender_pearl", "experience_bottle",
                "potion", "splash_potion", "lingering_potion", "fireball", "small_fireball", "wither_skull",
                "dragon_fireball", "llama_spit", "wind_charge", "breeze_wind_charge", "shulker_bullet",
                "firework_rocket");
        addAll(groups, EntityGroup.EFFECT,
                "area_effect_cloud", "evoker_fangs", "lightning_bolt");
        addAll(groups, EntityGroup.VEHICLE,
                "oak_boat", "oak_chest_boat", "acacia_boat", "acacia_chest_boat", "birch_boat",
                "birch_chest_boat", "cherry_boat", "cherry_chest_boat", "dark_oak_boat",
                "dark_oak_chest_boat", "jungle_boat", "jungle_chest_boat", "mangrove_boat",
                "mangrove_chest_boat", "pale_oak_boat", "pale_oak_chest_boat", "spruce_boat",
                "spruce_chest_boat", "bamboo_raft", "bamboo_chest_raft", "minecart", "chest_minecart",
                "furnace_minecart", "hopper_minecart", "spawner_minecart", "tnt_minecart",
                "command_block_minecart");
        addAll(groups, EntityGroup.MISC,
                "item", "item_frame", "glow_item_frame", "painting", "leash_knot", "interaction", "marker",
                "mannequin", "armor_stand", "end_crystal", "falling_block", "experience_orb", "eye_of_ender",
                "text_display", "block_display", "item_display", "fishing_bobber", "ominous_item_spawner", "tnt");
        return Map.copyOf(groups);
    }

    private static void addAll(Map<EntityType<?>, EntityGroup> groups, EntityGroup group, String... ids) {
        for (String id : ids) {
            Identifier identifier = Identifier.withDefaultNamespace(id);
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(identifier)) {
                groups.put(BuiltInRegistries.ENTITY_TYPE.getValue(identifier), group);
            }
        }
    }
}
