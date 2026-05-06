package io.github.pingisfun.hitboxplus.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;

public enum HitboxPattern implements NameableEnum {
    FULL("Full"),
    DOTTED("Dotted");

    private final Component displayName;

    HitboxPattern(String displayName) {
        this.displayName = Component.literal(displayName);
    }

    @Override
    public Component getDisplayName() {
        return displayName;
    }
}
