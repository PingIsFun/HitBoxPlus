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

    @ConfigEntry.Category(value = "players")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public Player player = new Player();

    @ConfigEntry.Category(value = "entity")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public Entity entity = new Entity();

    public static class Player {
        public boolean isEnabled = false;

        @ConfigEntry.Category(value = "players")
        @ConfigEntry.Gui.CollapsibleObject
        public FriendPlayer friend = new FriendPlayer();

        @ConfigEntry.Category(value = "players")
        @ConfigEntry.Gui.CollapsibleObject
        public EnemyPlayer enemy = new EnemyPlayer();

        @ConfigEntry.Category(value = "players")
        @ConfigEntry.Gui.CollapsibleObject
        public NeutralPlayer neutral = new NeutralPlayer();

        @ConfigEntry.Category(value = "players")
        @ConfigEntry.Gui.CollapsibleObject
        public SelfPlayer self = new SelfPlayer();


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
        @ConfigEntry.Gui.CollapsibleObject
        public PassiveMobEntity passive = new PassiveMobEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public HostileMobEntity hostile = new HostileMobEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public DecorationEntity decoration = new DecorationEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public ProjectileEntity projectile = new ProjectileEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public VehicleEntity vehicle = new VehicleEntity();

        @ConfigEntry.Gui.CollapsibleObject
        public MiscEntitiy misc = new MiscEntitiy();

        public static class MiscEntitiy {
            public boolean isEnabled = false;

            @ConfigEntry.Gui.CollapsibleObject
            public AreaEffectCloudEntity areaEffectCloud = new AreaEffectCloudEntity();

            @ConfigEntry.Gui.CollapsibleObject
            public ExperienceOrbEntity experienceOrb = new ExperienceOrbEntity();

            @ConfigEntry.Gui.CollapsibleObject
            public EyeOfEnderEntity eyeOfEnder = new EyeOfEnderEntity();

            @ConfigEntry.Gui.CollapsibleObject
            public FallingBlockEntity fallingBlock = new FallingBlockEntity();

            @ConfigEntry.Gui.CollapsibleObject
            public ItemEntity item = new ItemEntity();

            @ConfigEntry.Gui.CollapsibleObject
            public TntEntity tnt = new TntEntity();

            @ConfigEntry.Gui.CollapsibleObject
            public EndCrystalEntity endCrystalEntity = new EndCrystalEntity();



            public static class AreaEffectCloudEntity {
                @ConfigEntry.ColorPicker()
                public int color = 0xFFFFFF;

                @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
                public int alpha = 10;
            }
            public static class ExperienceOrbEntity {
                @ConfigEntry.ColorPicker()
                public int color = 0xFFFFFF;

                @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
                public int alpha = 10;
            }
            public static class EyeOfEnderEntity {
                @ConfigEntry.ColorPicker()
                public int color = 0xFFFFFF;

                @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
                public int alpha = 10;
            }
            public static class FallingBlockEntity {
                @ConfigEntry.ColorPicker()
                public int color = 0xFFFFFF;

                @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
                public int alpha = 10;
            }
            public static class ItemEntity {
                @ConfigEntry.ColorPicker()
                public int color = 0xFFFFFF;

                @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
                public int alpha = 10;
            }
            public static class TntEntity {
                @ConfigEntry.ColorPicker()
                public int color = 0xFFFFFF;

                @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
                public int alpha = 10;
            }
            public static class EndCrystalEntity {
                @ConfigEntry.ColorPicker()
                public int color = 0xFFFFFF;

                @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
                public int alpha = 10;
            }


        }

        public static class PassiveMobEntity {
            public boolean isEnabled = false;

            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }
        public static class HostileMobEntity {
            public boolean isEnabled = false;

            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

        public static class DecorationEntity {
            public boolean isEnabled = false;

            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }
        public static class ProjectileEntity {
            public boolean isEnabled = false;

            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

        public static class VehicleEntity {
            public boolean isEnabled = false;

            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

    }
}