package io.github.pingisfun.hitboxplus.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum HitboxPattern implements NameableEnum {
    FULL("Full"),
    DOTTED("Dotted");

    private final Text displayName;

    HitboxPattern(String displayName) {
        this.displayName = Text.literal(displayName);
    }

    @Override
    public Text getDisplayName() {
        return displayName;
    }
}
