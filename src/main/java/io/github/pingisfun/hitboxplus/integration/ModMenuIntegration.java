package io.github.pingisfun.hitboxplus.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.pingisfun.hitboxplus.HitBoxPlus;

public final class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return HitBoxPlus::createConfigScreen;
    }
}
