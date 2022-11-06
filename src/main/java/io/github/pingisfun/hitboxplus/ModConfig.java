package io.github.pingisfun.hitboxplus;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = HitboxPlus.MOD_ID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Category(value = "general")
    public boolean isModEnabled = true;

    @ConfigEntry.Category(value = "general")
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.ColorPicker()
    public int color = 0xFFFFFF;

    @ConfigEntry.Category(value = "general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
    public int alpha = 10;


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Category(value = "players")
    public boolean isPlayerConfigEnabled = false;

    @ConfigEntry.Category(value = "players")
    @ConfigEntry.Gui.CollapsibleObject
    public Player.ListConfig friend = new Player.ListConfig(0x20FF00);

    @ConfigEntry.Category(value = "players")
    @ConfigEntry.Gui.CollapsibleObject
    public Player.ListConfig enemy = new Player.ListConfig(0xD40000);

    @ConfigEntry.Category(value = "players")
    @ConfigEntry.Gui.CollapsibleObject
    public Player.SingleConfig neutral = new Player.SingleConfig();

    @ConfigEntry.Category(value = "players")
    @ConfigEntry.Gui.CollapsibleObject
    public Player.SingleConfig self = new Player.SingleConfig();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity passive = new Entity();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity hostile = new Entity();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity decoration = new Entity();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity projectile = new Entity();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity vehicle = new Entity();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public EnderDragonEntity ender_dragon = new EnderDragonEntity();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public MiscEntityDropdown misc = new MiscEntityDropdown();

    public static class MiscEntityDropdown {
        public boolean isEnabled = false;

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity areaEffectCloud = new MiscEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity experienceOrb = new MiscEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity eyeOfEnder = new MiscEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity fallingBlock = new MiscEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity item = new MiscEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity tnt = new MiscEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity endCrystalEntity = new MiscEntity();


    }

    public static class Player {
        public static class ListConfig {

            public ListConfig(int color) {
                this.color = color;
            }

            public List<String> list = new ArrayList<>();

            @ConfigEntry.ColorPicker()
            public int color;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }
        public static class SingleConfig {
            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

    }

    public static class Entity {
        public boolean isEnabled = false;

        @ConfigEntry.ColorPicker()
        public int color = 0xFFFFFF;

        @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
        public int alpha = 10;
    }

    public static class MiscEntity {
        @ConfigEntry.ColorPicker()
        public int color = 0xFFFFFF;

        @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
        public int alpha = 10;
    }

    public static class EnderDragonEntity {
        public boolean isEnabled = true;
        public boolean realHitbox = true;
        public boolean boxHitbox = false;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.ColorPicker()
        public int color = 0xFFFFFF;

        @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
        public int alpha = 10;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.ColorPicker()
        public int part_color = 0xFFFFFF;

        @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
        public int part_alpha = 10;
    }

}