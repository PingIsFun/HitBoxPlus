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
    @ConfigEntry.Gui.CollapsibleObject
    public Players players = new Players();



    public static class Players {
        boolean isPlayersEnabled = true;

        @ConfigEntry.Category(value = "general")
        @ConfigEntry.Gui.CollapsibleObject
        public FriendPlayer friend = new FriendPlayer();
        @ConfigEntry.Category(value = "general")
        @ConfigEntry.Gui.CollapsibleObject
        public EnemyPlayer enemy = new EnemyPlayer();
        @ConfigEntry.Category(value = "general")
        @ConfigEntry.Gui.CollapsibleObject
        public NeutralPlayer neutral = new NeutralPlayer();
        @ConfigEntry.Category(value = "general")
        @ConfigEntry.Gui.CollapsibleObject
        public SelfPlayer self = new SelfPlayer();

        // Friends
        public static class FriendPlayer {
            @ConfigEntry.Gui.PrefixText
            List<String> friends = new ArrayList<>();

            @ConfigEntry.ColorPicker()
            public int color = 0x20FF00;

            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

        public static class EnemyPlayer {
            @ConfigEntry.Gui.PrefixText
            List<String> enemies = new ArrayList<>();

            @ConfigEntry.ColorPicker()
            public int color = 0xD40000;

            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

        public static class SelfPlayer {
            @ConfigEntry.Gui.PrefixText
            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

        public static class NeutralPlayer {

            @ConfigEntry.Gui.PrefixText
            @ConfigEntry.ColorPicker()
            public int color = 0xFFFFFF;

            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
            public int alpha = 10;
        }

    }


}