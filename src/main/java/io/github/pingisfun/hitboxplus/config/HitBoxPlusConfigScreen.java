package io.github.pingisfun.hitboxplus.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HitBoxPlusConfigScreen {
    private HitBoxPlusConfigScreen() {
    }

    public static Screen create(Screen parent, HitBoxPlusConfig config) {
        HitBoxPlusConfig defaults = HitBoxPlusConfigManager.defaults();

        YetAnotherConfigLib yacl = YetAnotherConfigLib.createBuilder()
                .title(Text.literal("HitBox+"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("General"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enable mod"))
                                        .binding(defaults.isEnabled(), config::isEnabled, config::setEnabled)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<java.awt.Color>createBuilder()
                                        .name(Text.literal("Default hitbox color"))
                                        .binding(
                                                defaults.defaultHitbox.toAwtColor(),
                                                () -> config.defaultHitbox.toAwtColor(),
                                                config.defaultHitbox::setAwtColor
                                        )
                                        .controller(option -> ColorControllerBuilder.create(option).allowAlpha(false))
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Players"))
                        .groups(createPlayerGroups(config, defaults))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Groups"))
                        .groups(createGroupOptions(config, defaults))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Entities"))
                        .groups(createEntityOptions(config, defaults))
                        .build())
                .save(HitBoxPlusConfigManager::save)
                .build();

        return yacl.generateScreen(parent);
    }

    private static List<OptionGroup> createPlayerGroups(HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return List.of(
                OptionGroup.createBuilder()
                        .name(Text.literal("Player colors"))
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
                    .option(Option.<Boolean>createBuilder()
                            .name(Text.literal("Enabled"))
                            .binding(defaultConfig.enabled, () -> groupConfig.enabled, value -> groupConfig.enabled = value)
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                    .option(colorOption("Color", defaultConfig.color, groupConfig.color))
                    .build());
        }
        return groups;
    }

    private static List<OptionGroup> createEntityOptions(HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return Registries.ENTITY_TYPE.stream()
                .filter(entityType -> entityType != EntityType.PLAYER)
                .sorted(Comparator.comparing(entityType -> entityType.getName().getString()))
                .map(entityType -> {
                    String id = Registries.ENTITY_TYPE.getId(entityType).toString();
                    return OptionGroup.createBuilder()
                            .name(entityType.getName())
                            .collapsed(true)
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.literal("Enabled"))
                                    .binding(false, () -> config.isEntityOverrideEnabled(id), enabled -> config.setEntityOverrideEnabled(id, enabled))
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<java.awt.Color>createBuilder()
                                    .name(Text.literal("Override color"))
                                    .binding(
                                            defaults.defaultHitbox.toAwtColor(),
                                            () -> config.getEntityOverrideColor(id),
                                            color -> config.setEntityOverride(id, color)
                                    )
                                    .controller(option -> ColorControllerBuilder.create(option).allowAlpha(false))
                                    .build())
                            .build();
                })
                .toList();
    }

    private static Option<java.awt.Color> colorOption(String name, HitboxColorConfig defaults, HitboxColorConfig config) {
        return Option.<java.awt.Color>createBuilder()
                .name(Text.literal(name))
                .binding(defaults.toAwtColor(), config::toAwtColor, config::setAwtColor)
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(false))
                .build();
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
