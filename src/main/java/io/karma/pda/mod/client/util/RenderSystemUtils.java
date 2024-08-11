/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.karma.pda.mod.PDAMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Allows accessing the instance of {@link RenderSystem}
 * injected by {@link io.karma.pda.mod.client.mixin.RenderSystemMixin}.
 *
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RenderSystemUtils {
    private static RenderSystem instance;

    static {
        try {
            final var field = RenderSystem.class.getDeclaredField("pda$instance");
            field.setAccessible(true);
            instance = (RenderSystem) field.get(null);
            field.setAccessible(false);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not retrieve injected RenderSystem instance", error);
        }
    }

    public static RenderSystem getInstance() {
        return instance;
    }
}
