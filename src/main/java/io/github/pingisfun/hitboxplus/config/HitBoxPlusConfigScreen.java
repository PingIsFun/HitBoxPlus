package io.github.pingisfun.hitboxplus.config;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.ChatFormatting;

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
                .title(Component.literal("HitBox+"))
                .save(HitBoxPlusConfigManager::save);

        List<ConfigCategory> categories = List.of(
                generalCategory(parent, config, defaults),
                playersCategory(parent, config, defaults),
                groupsCategory(parent, config, defaults),
                entitiesCategory(parent, config, defaults)
        );

        if (initialCategory != InitialCategory.GENERAL) {
            builder.screenInit(screen -> screen.tabNavigationBar.selectTab(initialCategory.index, false));
        }
        categories.forEach(builder::category);

        YetAnotherConfigLib yacl = builder.build();

        return yacl.generateScreen(parent);
    }

    private static ConfigCategory generalCategory(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .tooltip(Component.literal("Global behavior and fallback color."))
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("General"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Enable mod"))
                                .description(description("Turn all custom hitbox colors on or off."))
                                .binding(defaults.isEnabled(), config::isEnabled, config::setEnabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(styleRow(parent, "Default hitbox", defaults.defaultHitbox, config.defaultHitbox, InitialCategory.GENERAL))
                        .build())
                .build();
    }

    private static ConfigCategory playersCategory(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("Players"))
                .tooltip(Component.literal("Self, friend, and enemy player colors."))
                .options(createPlayerOptions(parent, config, defaults))
                .group(playerList("Friends", defaults.friends, () -> config.friends, value -> config.friends = new ArrayList<>(value)))
                .group(playerList("Enemies", defaults.enemies, () -> config.enemies, value -> config.enemies = new ArrayList<>(value)))
                .build();
    }

    private static ConfigCategory groupsCategory(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("Groups"))
                .tooltip(Component.literal("Broad defaults for passive mobs, hostile mobs, bosses, and other groups."))
                .groups(createGroupOptions(parent, config, defaults))
                .build();
    }

    private static ConfigCategory entitiesCategory(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("Entities"))
                .tooltip(Component.literal("Per-entity overrides. Active overrides are shown first."))
                .groups(createEntityOptions(parent, config, defaults))
                .build();
    }

    private static List<Option<?>> createPlayerOptions(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return List.of(
                Option.<Boolean>createBuilder()
                        .name(Component.literal("Use team colors"))
                        .description(description("Warning: friend/enemy player styles override team colors. Team colors override neutral player hitboxes."))
                        .binding(defaults.usePlayerTeamColors, () -> config.usePlayerTeamColors, value -> config.usePlayerTeamColors = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build(),
                styleRow(parent, "Self", defaults.selfPlayer, config.selfPlayer, InitialCategory.PLAYERS),
                styleRow(parent, "Neutral", defaults.neutralPlayer, config.neutralPlayer, InitialCategory.PLAYERS),
                styleRow(parent, "Friends", defaults.friendPlayer, config.friendPlayer, InitialCategory.PLAYERS),
                styleRow(parent, "Enemies", defaults.enemyPlayer, config.enemyPlayer, InitialCategory.PLAYERS)
        );
    }

    private static List<OptionGroup> createGroupOptions(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        OptionGroup.Builder builder = OptionGroup.createBuilder()
                .name(Component.literal("Groups"))
                .description(description("Each group can be enabled or disabled, and its style can be edited."))
                .collapsed(false);
        for (EntityGroup group : EntityGroup.values()) {
            GroupHitboxConfig groupConfig = config.groupHitboxes.get(group);
            GroupHitboxConfig defaultConfig = defaults.groupHitboxes.get(group);
            builder.option(twoActionRow(
                    group.displayName(),
                    "Edit",
                    () -> Minecraft.getInstance().setScreen(styleScreen(
                            create(parent, HitBoxPlusConfigManager.config(), InitialCategory.GROUPS),
                            group.displayName(),
                            defaultConfig.color,
                            groupConfig.color
                    )),
                    () -> groupConfig.enabled ? "Enabled" : "Disabled",
                    () -> groupConfig.enabled ? ChatFormatting.GREEN : ChatFormatting.RED,
                    () -> {
                        groupConfig.enabled = !groupConfig.enabled;
                        HitBoxPlusConfigManager.save();
                    }
            ));
        }
        return List.of(builder.build());
    }

    private static List<OptionGroup> createEntityOptions(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        List<OptionGroup> groups = new ArrayList<>();
        List<String> activeIds = config.entityOverrides.entrySet().stream()
                .filter(entry -> entry.getValue().enabled)
                .map(Map.Entry::getKey)
                .filter(HitBoxPlusConfigScreen::entityTypeExists)
                .sorted(Comparator.comparing(HitBoxPlusConfigScreen::entityDisplayName))
                .toList();

        groups.add(activeEntityOverridesGroup(parent, config, defaults, activeIds));
        groups.add(addEntityOverridesGroup(parent, config, defaults, activeIds));
        return groups;
    }

    private static OptionGroup activeEntityOverridesGroup(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults, List<String> activeIds) {
        OptionGroup.Builder builder = OptionGroup.createBuilder()
                .name(Component.literal("Active overrides"))
                .description(description("Only enabled entity-specific rules are shown here."))
                .collapsed(false);

        if (activeIds.isEmpty()) {
            builder.option(ButtonOption.createBuilder()
                    .name(Component.literal("No active overrides"))
                    .text(Component.literal("None"))
                    .available(false)
                    .action((screen, option) -> {
                    })
                    .build());
            return builder.build();
        }

        for (String id : activeIds) {
            EntityHitboxConfig override = config.entityOverride(id);
            String displayName = entityDisplayName(id);
            builder.option(entityOverrideRow(
                    parent,
                    displayName,
                    defaults.defaultHitbox,
                    override.color,
                    () -> {
                        config.setEntityOverrideEnabled(id, false);
                        refresh(parent);
                    }
            ));
        }

        return builder.build();
    }

    private static OptionGroup addEntityOverridesGroup(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults, List<String> activeIds) {
        OptionGroup.Builder builder = OptionGroup.createBuilder()
                .name(Component.literal("Add entity override"))
                .description(description("Shows every entity without an active override. Added entities move to Active overrides."))
                .collapsed(false);

        for (String id : availableEntityIds(activeIds)) {
            if (activeIds.contains(id)) {
                continue;
            }

            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(id));
            builder.option(ButtonOption.createBuilder()
                    .name(entityType.getDescription())
                    .text(Component.literal("Add"))
                    .action((screen, option) -> {
                        config.enableEntityOverride(id);
                        refresh(parent);
                    })
                    .build());
        }

        builder.option(Option.<String>createBuilder()
                .name(Component.literal("Custom entity id"))
                .description(description("Use a full entity id such as minecraft:zombie. Saving validates the id."))
                .binding("", () -> "", value -> {
                    String id = value.strip();
                    Identifier identifier = Identifier.tryParse(id);
                    if (identifier != null && BuiltInRegistries.ENTITY_TYPE.containsKey(identifier)) {
                        config.enableEntityOverride(id);
                        HitBoxPlusConfigManager.save();
                    }
                })
                .controller(option -> StringControllerBuilder.create(option))
                .build());

        return builder.build();
    }

    private static List<String> availableEntityIds(List<String> activeIds) {
        return BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(entityType -> entityType != EntityType.PLAYER)
                .map(entityType -> BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString())
                .filter(id -> !activeIds.contains(id))
                .sorted(Comparator.comparing(HitBoxPlusConfigScreen::entityDisplayName))
                .toList();
    }

    private static ButtonOption styleRow(
            Screen parent,
            String name,
            HitboxColorConfig defaults,
            HitboxColorConfig config,
            InitialCategory returnCategory
    ) {
        return styleRow(parent, name, defaults, config, returnCategory, name);
    }

    private static ButtonOption styleRow(
            Screen parent,
            String rowName,
            HitboxColorConfig defaults,
            HitboxColorConfig config,
            InitialCategory returnCategory,
            String screenName
    ) {
        return ButtonOption.createBuilder()
                .name(Component.literal(rowName))
                .text(Component.literal("Edit"))
                .action((screen, option) -> Minecraft.getInstance().setScreen(
                        styleScreen(
                                create(parent, HitBoxPlusConfigManager.config(), returnCategory),
                                screenName,
                                defaults,
                                config
                        )
                ))
                .build();
    }

    private static Screen styleScreen(Screen parent, String name, HitboxColorConfig defaults, HitboxColorConfig config) {
        YetAnotherConfigLib yacl = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("HitBox+ - " + name))
                .save(HitBoxPlusConfigManager::save)
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Style"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal(name))
                                .options(styleOptions("", defaults, config))
                                .build())
                        .build())
                .build();

        return yacl.generateScreen(parent);
    }

    private static Option<TwoActionRow> entityOverrideRow(
            Screen parent,
            String displayName,
            HitboxColorConfig defaults,
            HitboxColorConfig config,
            Runnable disableAction
    ) {
        return twoActionRow(
                displayName,
                "Edit",
                () -> Minecraft.getInstance().setScreen(styleScreen(
                        create(parent, HitBoxPlusConfigManager.config(), InitialCategory.ENTITIES),
                        displayName,
                        defaults,
                        config
                )),
                () -> "Remove",
                () -> ChatFormatting.RED,
                disableAction
        );
    }

    private static Option<TwoActionRow> twoActionRow(
            String rowName,
            String primaryText,
            Runnable primaryAction,
            java.util.function.Supplier<String> secondaryText,
            java.util.function.Supplier<ChatFormatting> secondaryColor,
            Runnable secondaryAction
    ) {
        TwoActionRow actions = new TwoActionRow(primaryText, primaryAction, secondaryText, secondaryColor, secondaryAction);
        return Option.<TwoActionRow>createBuilder()
                .name(Component.literal(rowName))
                .binding(actions, () -> actions, ignored -> {
                })
                .customController(TwoActionRowController::new)
                .build();
    }

    private static List<Option<?>> styleOptions(String prefix, HitboxColorConfig defaults, HitboxColorConfig config) {
        return List.of(
                colorOption(prefix + "Color", defaults, config),
                Option.<Boolean>createBuilder()
                        .name(Component.literal(prefix + "Show hitbox"))
                        .binding(defaults.showHitbox, () -> config.showHitbox, value -> config.showHitbox = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build(),
                Option.<Float>createBuilder()
                        .name(Component.literal(prefix + "Thickness"))
                        .binding(defaults.hitboxThickness, () -> config.hitboxThickness, value -> config.hitboxThickness = value)
                        .controller(option -> FloatSliderControllerBuilder.create(option)
                                .range(HitboxColorConfig.MIN_HITBOX_THICKNESS, HitboxColorConfig.MAX_HITBOX_THICKNESS)
                                .step(0.5F)
                                .formatValue(value -> Component.literal(String.format(java.util.Locale.ROOT, "%.1f", value))))
                        .build(),
                Option.<HitboxPattern>createBuilder()
                        .name(Component.literal(prefix + "Pattern"))
                        .binding(defaults.hitboxPattern, () -> config.hitboxPattern, value -> config.hitboxPattern = value)
                        .controller(option -> EnumControllerBuilder.create(option).enumClass(HitboxPattern.class))
                        .build(),
                Option.<Boolean>createBuilder()
                        .name(Component.literal(prefix + "Eye line"))
                        .binding(defaults.showEyeLine, () -> config.showEyeLine, value -> config.showEyeLine = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build(),
                Option.<Boolean>createBuilder()
                        .name(Component.literal(prefix + "Look direction"))
                        .binding(defaults.showLookDirection, () -> config.showLookDirection, value -> config.showLookDirection = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build()
        );
    }

    private static Option<java.awt.Color> colorOption(String name, HitboxColorConfig defaults, HitboxColorConfig config) {
        return Option.<java.awt.Color>createBuilder()
                .name(Component.literal(name))
                .binding(defaults.toAwtColor(), config::toAwtColor, config::setAwtColor)
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(false))
                .build();
    }

    private static OptionDescription description(String text) {
        return OptionDescription.of(Component.literal(text));
    }

    private static String entityDisplayName(String entityId) {
        Identifier id = Identifier.tryParse(entityId);
        if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
            return entityId;
        }

        return BuiltInRegistries.ENTITY_TYPE.getValue(id).getDescription().getString();
    }

    private static boolean entityTypeExists(String entityId) {
        Identifier id = Identifier.tryParse(entityId);
        return id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id);
    }

    private static void refresh(Screen parent) {
        HitBoxPlusConfigManager.save();
        Minecraft.getInstance().setScreen(create(parent, HitBoxPlusConfigManager.config(), InitialCategory.ENTITIES));
    }

    private enum InitialCategory {
        GENERAL(0),
        PLAYERS(1),
        GROUPS(2),
        ENTITIES(3);

        private final int index;

        InitialCategory(int index) {
            this.index = index;
        }
    }

    private static ListOption<String> playerList(
            String name,
            List<String> defaults,
            java.util.function.Supplier<List<String>> getter,
            java.util.function.Consumer<List<String>> setter
    ) {
        return ListOption.<String>createBuilder()
                .name(Component.literal(name))
                .binding(defaults, getter, setter)
                .controller(StringControllerBuilder::create)
                .initial("")
                .insertEntriesAtEnd(true)
                .build();
    }

    private record TwoActionRow(
            String primaryText,
            Runnable primaryAction,
            java.util.function.Supplier<String> secondaryText,
            java.util.function.Supplier<ChatFormatting> secondaryColor,
            Runnable secondaryAction
    ) {
    }

    private static final class TwoActionRowController implements Controller<TwoActionRow> {
        private final Option<TwoActionRow> option;

        private TwoActionRowController(Option<TwoActionRow> option) {
            this.option = option;
        }

        @Override
        public Option<TwoActionRow> option() {
            return option;
        }

        @Override
        public Component formatValue() {
            return Component.literal(option.pendingValue().primaryText());
        }

        @Override
        public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
            return new TwoActionRowWidget(this, screen, widgetDimension);
        }
    }

    private static final class TwoActionRowWidget extends ControllerWidget<TwoActionRowController> {
        private static final int BUTTON_WIDTH = 68;
        private static final int BUTTON_GAP = 4;

        private TwoActionRowWidget(TwoActionRowController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        protected void extractValueText(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
            TwoActionRow actions = control.option().pendingValue();
            drawActionButton(context, primaryX(), Component.literal(actions.primaryText()), 0xFFFFFFFF, isPrimaryHovered(mouseX, mouseY));
            drawActionButton(context, secondaryX(), Component.literal(actions.secondaryText().get()), textColor(actions.secondaryColor().get()), isSecondaryHovered(mouseX, mouseY));
            if (hovered) {
                context.requestCursor(isAvailable() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
            }
        }

        private void drawActionButton(GuiGraphicsExtractor context, int x, Component text, int color, boolean hovered) {
            int y = getDimension().y() + getYPadding();
            int height = getDimension().height() - getYPadding() * 2;
            drawButtonRect(context, x, y, x + BUTTON_WIDTH, y + height, hovered && isAvailable(), isAvailable());
            context.centeredText(textRenderer, text, x + BUTTON_WIDTH / 2, getTextY(), isAvailable() ? color : inactiveColor);
        }

        private int textColor(ChatFormatting formatting) {
            Integer color = formatting.getColor();
            return color == null ? 0xFFFFFFFF : 0xFF000000 | color;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!isAvailable()) {
                return false;
            }
            double mouseX = event.x();
            double mouseY = event.y();
            if (isPrimaryHovered(mouseX, mouseY)) {
                playDownSound();
                control.option().pendingValue().primaryAction().run();
                return true;
            }
            if (isSecondaryHovered(mouseX, mouseY)) {
                playDownSound();
                control.option().pendingValue().secondaryAction().run();
                return true;
            }
            return false;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        protected int getUnhoveredControlWidth() {
            return BUTTON_WIDTH * 2 + BUTTON_GAP;
        }

        @Override
        public boolean canReset() {
            return false;
        }

        @Override
        public boolean matchesSearch(String query) {
            String lowerQuery = query.toLowerCase();
            TwoActionRow actions = control.option().pendingValue();
            return super.matchesSearch(query)
                    || actions.primaryText().toLowerCase().contains(lowerQuery)
                    || actions.secondaryText().get().toLowerCase().contains(lowerQuery);
        }

        private boolean isPrimaryHovered(double mouseX, double mouseY) {
            return isButtonHovered(mouseX, mouseY, primaryX());
        }

        private boolean isSecondaryHovered(double mouseX, double mouseY) {
            return isButtonHovered(mouseX, mouseY, secondaryX());
        }

        private boolean isButtonHovered(double mouseX, double mouseY, int x) {
            int y = getDimension().y() + getYPadding();
            int height = getDimension().height() - getYPadding() * 2;
            return mouseX >= x && mouseX <= x + BUTTON_WIDTH && mouseY >= y && mouseY <= y + height;
        }

        private int primaryX() {
            return getDimension().xLimit() - getXPadding() - BUTTON_WIDTH * 2 - BUTTON_GAP;
        }

        private int secondaryX() {
            return getDimension().xLimit() - getXPadding() - BUTTON_WIDTH;
        }
    }
}
