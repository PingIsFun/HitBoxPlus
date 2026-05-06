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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
                        .option(styleRow(parent, "Default hitbox", defaults.defaultHitbox, config.defaultHitbox, InitialCategory.GENERAL))
                        .build())
                .build();
    }

    private static ConfigCategory playersCategory(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("Players"))
                .tooltip(Text.literal("Self, friend, and enemy player colors."))
                .options(createPlayerOptions(parent, config, defaults))
                .group(playerList("Friends", defaults.friends, () -> config.friends, value -> config.friends = new ArrayList<>(value)))
                .group(playerList("Enemies", defaults.enemies, () -> config.enemies, value -> config.enemies = new ArrayList<>(value)))
                .build();
    }

    private static ConfigCategory groupsCategory(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("Groups"))
                .tooltip(Text.literal("Broad defaults for passive mobs, hostile mobs, bosses, and other groups."))
                .groups(createGroupOptions(parent, config, defaults))
                .build();
    }

    private static ConfigCategory entitiesCategory(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("Entities"))
                .tooltip(Text.literal("Per-entity overrides. Active overrides are shown first."))
                .groups(createEntityOptions(parent, config, defaults))
                .build();
    }

    private static List<Option<?>> createPlayerOptions(Screen parent, HitBoxPlusConfig config, HitBoxPlusConfig defaults) {
        return List.of(
                Option.<Boolean>createBuilder()
                        .name(Text.literal("Use team colors"))
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
                .name(Text.literal("Groups"))
                .description(description("Each group can be enabled or disabled, and its style can be edited."))
                .collapsed(false);
        for (EntityGroup group : EntityGroup.values()) {
            GroupHitboxConfig groupConfig = config.groupHitboxes.get(group);
            GroupHitboxConfig defaultConfig = defaults.groupHitboxes.get(group);
            builder.option(twoActionRow(
                    group.displayName(),
                    "Edit",
                    () -> MinecraftClient.getInstance().setScreen(styleScreen(
                            create(parent, HitBoxPlusConfigManager.config(), InitialCategory.GROUPS),
                            group.displayName(),
                            defaultConfig.color,
                            groupConfig.color
                    )),
                    () -> groupConfig.enabled ? "Enabled" : "Disabled",
                    () -> groupConfig.enabled ? Formatting.GREEN : Formatting.RED,
                    () -> groupConfig.enabled = !groupConfig.enabled
            ));
        }
        return List.of(builder.build());
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
                .name(Text.literal(rowName))
                .text(Text.literal("Edit"))
                .action((screen, option) -> MinecraftClient.getInstance().setScreen(
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
                .title(Text.literal("HitBox+ - " + name))
                .save(HitBoxPlusConfigManager::save)
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Style"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal(name))
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
                () -> MinecraftClient.getInstance().setScreen(styleScreen(
                        create(parent, HitBoxPlusConfigManager.config(), InitialCategory.ENTITIES),
                        displayName,
                        defaults,
                        config
                )),
                () -> "Remove",
                () -> Formatting.RED,
                disableAction
        );
    }

    private static Option<TwoActionRow> twoActionRow(
            String rowName,
            String primaryText,
            Runnable primaryAction,
            java.util.function.Supplier<String> secondaryText,
            java.util.function.Supplier<Formatting> secondaryColor,
            Runnable secondaryAction
    ) {
        TwoActionRow actions = new TwoActionRow(primaryText, primaryAction, secondaryText, secondaryColor, secondaryAction);
        return Option.<TwoActionRow>createBuilder()
                .name(Text.literal(rowName))
                .binding(actions, () -> actions, ignored -> {
                })
                .customController(TwoActionRowController::new)
                .build();
    }

    private static List<Option<?>> styleOptions(String prefix, HitboxColorConfig defaults, HitboxColorConfig config) {
        return List.of(
                colorOption(prefix + "Color", defaults, config),
                Option.<Boolean>createBuilder()
                        .name(Text.literal(prefix + "Show hitbox"))
                        .binding(defaults.showHitbox, () -> config.showHitbox, value -> config.showHitbox = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build(),
                Option.<Float>createBuilder()
                        .name(Text.literal(prefix + "Thickness"))
                        .binding(defaults.hitboxThickness, () -> config.hitboxThickness, value -> config.hitboxThickness = value)
                        .controller(option -> FloatSliderControllerBuilder.create(option)
                                .range(HitboxColorConfig.MIN_HITBOX_THICKNESS, HitboxColorConfig.MAX_HITBOX_THICKNESS)
                                .step(0.5F)
                                .formatValue(value -> Text.literal(String.format(java.util.Locale.ROOT, "%.1f", value))))
                        .build(),
                Option.<HitboxPattern>createBuilder()
                        .name(Text.literal(prefix + "Pattern"))
                        .binding(defaults.hitboxPattern, () -> config.hitboxPattern, value -> config.hitboxPattern = value)
                        .controller(option -> EnumControllerBuilder.create(option).enumClass(HitboxPattern.class))
                        .build(),
                Option.<Boolean>createBuilder()
                        .name(Text.literal(prefix + "Eye line"))
                        .binding(defaults.showEyeLine, () -> config.showEyeLine, value -> config.showEyeLine = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build(),
                Option.<Boolean>createBuilder()
                        .name(Text.literal(prefix + "Look direction"))
                        .binding(defaults.showLookDirection, () -> config.showLookDirection, value -> config.showLookDirection = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build()
        );
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
                .name(Text.literal(name))
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
            java.util.function.Supplier<Formatting> secondaryColor,
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
        public Text formatValue() {
            return Text.literal(option.pendingValue().primaryText());
        }

        @Override
        public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
            return new TwoActionRowWidget(this, screen, widgetDimension);
        }
    }

    private static final class TwoActionRowWidget extends ControllerWidget<TwoActionRowController> {
        private static final int BUTTON_WIDTH = 58;
        private static final int BUTTON_GAP = 4;

        private TwoActionRowWidget(TwoActionRowController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        protected void drawValueText(DrawContext context, int mouseX, int mouseY, float delta) {
            TwoActionRow actions = control.option().pendingValue();
            drawActionButton(context, primaryX(), Text.literal(actions.primaryText()), Formatting.WHITE, isPrimaryHovered(mouseX, mouseY));
            drawActionButton(context, secondaryX(), Text.literal(actions.secondaryText().get()), actions.secondaryColor().get(), isSecondaryHovered(mouseX, mouseY));
            if (hovered) {
                context.setCursor(isAvailable() ? net.minecraft.client.gui.cursor.StandardCursors.POINTING_HAND : net.minecraft.client.gui.cursor.StandardCursors.NOT_ALLOWED);
            }
        }

        private void drawActionButton(DrawContext context, int x, Text text, Formatting color, boolean hovered) {
            int y = getDimension().y() + getYPadding();
            int height = getDimension().height() - getYPadding() * 2;
            drawButtonRect(context, x, y, x + BUTTON_WIDTH, y + height, hovered && isAvailable(), isAvailable());
            context.drawCenteredTextWithShadow(textRenderer, text, x + BUTTON_WIDTH / 2, getTextY(), isAvailable() ? color.getColorValue() : inactiveColor);
        }

        @Override
        public boolean onMouseClicked(double mouseX, double mouseY, int button) {
            if (!isAvailable()) {
                return false;
            }
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
