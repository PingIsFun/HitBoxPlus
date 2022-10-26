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
    public Player.FriendPlayer friend = new Player.FriendPlayer();

    @ConfigEntry.Category(value = "players")
    @ConfigEntry.Gui.CollapsibleObject
    public Player.EnemyPlayer enemy = new Player.EnemyPlayer();

    @ConfigEntry.Category(value = "players")
    @ConfigEntry.Gui.CollapsibleObject
    public Player.NeutralPlayer neutral = new Player.NeutralPlayer();

    @ConfigEntry.Category(value = "players")
    @ConfigEntry.Gui.CollapsibleObject
    public Player.SelfPlayer self = new Player.SelfPlayer();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity passive = Entity.passive();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity hostile = Entity.hostile();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity decoration = Entity.decoration();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity projectile = Entity.projectile();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public Entity vehicle = Entity.vehicle();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public EnderDragonEntity ender_dragon = new EnderDragonEntity();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject
    public MiscEntityDropdown misc = new MiscEntityDropdown();

    public static class MiscEntityDropdown {
        public boolean isEnabled = false;

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity areaEffectCloud = MiscEntity.areaEffectCloud();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity experienceOrb = MiscEntity.experienceOrb();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity eyeOfEnder = MiscEntity.eyeOfEnder();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity fallingBlock = MiscEntity.fallingBlock();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity item = MiscEntity.item();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity tnt = MiscEntity.tnt();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntity endCrystalEntity = MiscEntity.endCrystalEntity();


    }

    public static class Player {
        public static class FriendPlayer {
            public List<String> list = new ArrayList<>();

            @ConfigEntry.ColorPicker()
            public int color = 0x20FF00;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

        public static class EnemyPlayer {
            public List<String> list = new ArrayList<>();

            @ConfigEntry.ColorPicker()
            public int color = 0xD40000;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

        public static class SelfPlayer {
            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

        public static class NeutralPlayer {

            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

    }

    public static class Entity {
        public static Entity passive() {
            return new Entity();
        }

        public static Entity hostile() {
            return new Entity();
        }

        public static Entity decoration() {
            return new Entity();
        }

        public static Entity projectile() {
            return new Entity();
        }

        public static Entity vehicle() {
            return new Entity();
        }

        public boolean isEnabled = false;

        @ConfigEntry.ColorPicker()
        public int color = 0xFFFFFF;

        @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
        public int alpha = 10;
    }

    public static class MiscEntity {
        public static MiscEntity areaEffectCloud() {
            return new MiscEntity();
        }

        public static MiscEntity experienceOrb() {
            return new MiscEntity();
        }

        public static MiscEntity eyeOfEnder() {
            return new MiscEntity();
        }

        public static MiscEntity fallingBlock() {
            return new MiscEntity();
        }

        public static MiscEntity item() {
            return new MiscEntity();
        }

        public static MiscEntity tnt() {
            return new MiscEntity();
        }

        public static MiscEntity endCrystalEntity() {
            return new MiscEntity();
        }

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