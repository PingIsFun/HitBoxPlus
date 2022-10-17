package io.github.pingisfun.hitboxplus;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = HitboxPlus.MOD_ID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Category(value = "general")
    boolean isModEnabled = true;

//    @ConfigEntry.Gui.CollapsibleObject
//    InnerStuff stuff = new InnerStuff();

    @ConfigEntry.Category(value = "general")
    @ConfigEntry.Gui.CollapsibleObject
    public Players players = new Players();


//    static class InnerStuff {
//        public int a = 0;
//        public int b = 1;
//    }

    public static class Players {
        boolean isPlayersEnabled = true;

        // Friends
        @ConfigEntry.Gui.PrefixText
        List<String> friends = new ArrayList<>();

        @ConfigEntry.ColorPicker()
        public int friend_color = 0x20FF00;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
        public int friend_alpha = 10;

        // Enemies
        @ConfigEntry.Gui.PrefixText
        List<String> enemies = new ArrayList<>();

        @ConfigEntry.ColorPicker()
        public int enemy_color = 0xD40000;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
        public int enemy_alpha = 10;

        // Neutral
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.ColorPicker()
        public int neutral_color = 0xFFFFFF;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
        public int neutral_alpha = 10;

        // Self
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.ColorPicker()
        public int self_color = 0xFFFFFF;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(max = 10, min = 0)
        public int self_alpha = 10;
    }


}