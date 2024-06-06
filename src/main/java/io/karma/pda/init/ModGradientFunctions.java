/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.init;

import io.karma.pda.PDAMod;
import io.karma.pda.api.color.GradientFunction;
import io.karma.pda.api.color.GradientType;
import io.karma.pda.api.util.Constants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class ModGradientFunctions {
    // @formatter:off
    private ModGradientFunctions() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void register() {
        PDAMod.LOGGER.debug("Registering gradient functions");
        for (final var type : GradientType.values()) {
            register(type.getName().getPath(), n -> type);
        }
    }

    private static void register(final String name, final Function<ResourceLocation, GradientFunction> factory) {
        PDAMod.GRADIENT_FUNCTIONS.register(name, () -> factory.apply(new ResourceLocation(Constants.MODID, name)));
    }
}
