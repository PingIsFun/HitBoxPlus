package io.github.pingisfun.hitboxplus.config;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class HitBoxPlusConfigScreen {
    private HitBoxPlusConfigScreen() {
    }

    public static Screen create(Screen parent, HitBoxPlusConfig config) {
        return create(parent, config, InitialCategory.GENERAL);
    }

    private static Screen create(Screen parent, HitBoxPlusConfig config, InitialCategory initialCategory) {
        HitBoxPlusConfig defaults = HitBoxPlusConfigManager.defaults();

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Text.literal("HitBox+"))
                .save(HitBoxPlusConfigManager::save);

        List<ConfigCategory> categories = List.of(
                generalCategory(config, defaults),
                playersCategory(config, defaults),
                groupsCategory(config, defaults),
                entitiesCategory(parent, config, defaults)
        );

        if (initialCategory == InitialCategory.ENTITIES) {
            builder.screenInit(screen -> screen.tabNavigationBar.selectTab(3, false));
        }
        categories.forEach(builder::category);

        YetAnotherConfigLib yacl = builder.build();

        return yacl.generateScreen(parent);
    }

    private static ConfigCategory generalCategory(HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("General"))
                .tooltip(Text.literal("Global behavior and fallback color."))
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("General"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Enable mod"))
                                .description(description("Turn all custom hitbox colors on or off."))
                                .binding(defaults.isEnabled(), config::isEnabled, config::setEnabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<java.awt.Color>createBuilder()
                                .name(Text.literal("Default hitbox color"))
                                .description(description("Used when no player, group, or entity-specific rule applies."))
                                .binding(
                                        defaults.defaultHitbox.toAwtColor(),
                                        () -> config.defaultHitbox.toAwtColor(),
                                        config.defaultHitbox::setAwtColor
                                )
                                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(false))
                                .build())
                        .build())
                .build();
    }

    private static ConfigCategory playersCategory(HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("Players"))
                .tooltip(Text.literal("Self, friend, and enemy player colors."))
                .groups(createPlayerGroups(config, defaults))
                .build();
    }

    private static ConfigCategory groupsCategory(HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("Groups"))
                .tooltip(Text.literal("Broad defaults for passive mobs, hostile mobs, bosses, and other groups."))
                .groups(createGroupOptions(config, defaults))
                .build();
    }

    private static ConfigCategory entitiesCategory(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("Entities"))
                .tooltip(Text.literal("Per-entity overrides. Active overrides are shown first."))
                .groups(createEntityOptions(parent, config, defaults))
                .build();
    }

    private static List<OptionGroup> createPlayerGroups(HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return List.of(
                OptionGroup.createBuilder()
                        .name(Text.literal("Player colors"))
                        .description(description("Player colors override entity and group colors."))
                        .option(colorOption("Self color", defaults.selfPlayer, config.selfPlayer))
                        .option(colorOption("Friend color", defaults.friendPlayer, config.friendPlayer))
                        .option(colorOption("Enemy color", defaults.enemyPlayer, config.enemyPlayer))
                        .build(),
                playerList("Friends", defaults.friends, () -> config.friends, value -> config.friends = new ArrayList<>(value)),
                playerList("Enemies", defaults.enemies, () -> config.enemies, value -> config.enemies = new ArrayList<>(value))
        );
    }

    private static List<OptionGroup> createGroupOptions(HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        List<OptionGroup> groups = new ArrayList<>();
        for (EntityGroup group : EntityGroup.values()) {
            GroupHitboxConfig groupConfig = config.groupHitboxes.get(group);
            GroupHitboxConfig defaultConfig = defaults.groupHitboxes.get(group);
            groups.add(OptionGroup.createBuilder()
                    .name(Text.literal(group.displayName()))
                    .collapsed(group != EntityGroup.PASSIVE && group != EntityGroup.HOSTILE && group != EntityGroup.BOSS)
                    .option(Option.<Boolean>createBuilder()
                            .name(Text.literal("Enabled"))
                            .description(description("When disabled, this group uses the global default unless an entity override exists."))
                            .binding(defaultConfig.enabled, () -> groupConfig.enabled, value -> groupConfig.enabled = value)
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                    .option(colorOption("Color", defaultConfig.color, groupConfig.color))
                    .build());
        }
        return groups;
    }

    private static List<OptionGroup> createEntityOptions(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        List<OptionGroup> groups = new ArrayList<>();
        List<String> activeIds = config.entityOverrides.entrySet().stream()
                .filter(entry -> entry.getValue().enabled)
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(HitBoxPlusConfigScreen::entityDisplayName))
                .toList();

        groups.add(activeEntityOverridesGroup(parent, config, defaults, activeIds));
        groups.add(addEntityOverridesGroup(parent, config, defaults, activeIds));
        return groups;
    }

    private static OptionGroup activeEntityOverridesGroup(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults, List<String> activeIds) {
        OptionGroup.Builder builder = OptionGroup.createBuilder()
                .name(Text.literal("Active overrides"))
                .description(description("Only enabled entity-specific rules are shown here."))
                .collapsed(false);

        if (activeIds.isEmpty()) {
            builder.option(ButtonOption.createBuilder()
                    .name(Text.literal("No active overrides"))
                    .text(Text.literal("None"))
                    .available(false)
                    .action((screen, option) -> {
                    })
                    .build());
            return builder.build();
        }

        for (String id : activeIds) {
            builder.option(Option.<java.awt.Color>createBuilder()
                    .name(Text.literal(entityDisplayName(id)))
                    .description(description(id))
                    .binding(
                            defaults.defaultHitbox.toAwtColor(),
                            () -> config.getEntityOverrideColor(id),
                            color -> config.setEntityOverride(id, color)
                    )
                    .controller(option -> ColorControllerBuilder.create(option).allowAlpha(false))
                    .build());
            builder.option(ButtonOption.createBuilder()
                    .name(Text.literal("Remove " + entityDisplayName(id)))
                    .text(Text.literal("Remove"))
                    .action((screen, option) -> {
                        config.removeEntityOverride(id);
                        refresh(parent);
                    })
                    .build());
        }

        return builder.build();
    }

    private static OptionGroup addEntityOverridesGroup(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults, List<String> activeIds) {
        OptionGroup.Builder builder = OptionGroup.createBuilder()
                .name(Text.literal("Add entity override"))
                .description(description("Shows every entity without an active override. Added entities move to Active overrides."))
                .collapsed(false);

        for (String id : availableEntityIds(activeIds)) {
            if (activeIds.contains(id)) {
                continue;
            }

            EntityType<?> entityType = Registries.ENTITY_TYPE.get(net.minecraft.util.Identifier.of(id));
            builder.option(ButtonOption.createBuilder()
                    .name(entityType.getName())
                    .text(Text.literal("Add"))
                    .action((screen, option) -> {
                        config.enableEntityOverride(id);
                        refresh(parent);
                    })
                    .build());
        }

        builder.option(Option.<String>createBuilder()
                .name(Text.literal("Custom entity id"))
                .description(description("Use a full entity id such as minecraft:zombie. Saving validates the id."))
                .binding("", () -> "", value -> {
                    String id = value.strip();
                    if (Registries.ENTITY_TYPE.containsId(net.minecraft.util.Identifier.tryParse(id))) {
                        config.enableEntityOverride(id);
                        HitBoxPlusConfigManager.save();
                    }
                })
                .controller(option -> StringControllerBuilder.create(option))
                .build());

        return builder.build();
    }

    private static List<String> availableEntityIds(List<String> activeIds) {
        return Registries.ENTITY_TYPE.stream()
                .filter(entityType -> entityType != EntityType.PLAYER)
                .map(entityType -> Registries.ENTITY_TYPE.getId(entityType).toString())
                .filter(id -> !activeIds.contains(id))
                .sorted(Comparator.comparing(HitBoxPlusConfigScreen::entityDisplayName))
                .toList();
    }

    private static Option<java.awt.Color> colorOption(String name, HitboxColorConfig defaults, HitboxColorConfig config) {
        return Option.<java.awt.Color>createBuilder()
                .name(Text.literal(name))
                .binding(defaults.toAwtColor(), config::toAwtColor, config::setAwtColor)
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(false))
                .build();
    }

    private static OptionDescription description(String text) {
        return OptionDescription.of(Text.literal(text));
    }

    private static String entityDisplayName(String entityId) {
        net.minecraft.util.Identifier id = net.minecraft.util.Identifier.tryParse(entityId);
        if (id == null || !Registries.ENTITY_TYPE.containsId(id)) {
            return entityId;
        }

        return Registries.ENTITY_TYPE.get(id).getName().getString();
    }

    private static void refresh(Screen parent) {
        HitBoxPlusConfigManager.save();
        MinecraftClient.getInstance().setScreen(create(parent, HitBoxPlusConfigManager.config(), InitialCategory.ENTITIES));
    }

    private enum InitialCategory {
        GENERAL,
        ENTITIES
    }

    private static ListOption<String> playerList(
            String name,
            List<String> defaults,
            java.util.function.Supplier<List<String>> getter,
            java.util.function.Consumer<List<String>> setter
    ) {
        return ListOption.<String>createBuilder()
                .name(Text.literal(name))
                .binding(defaults, getter, setter)
                .controller(StringControllerBuilder::create)
                .initial("")
                .insertEntriesAtEnd(true)
                .build();
    }
}
