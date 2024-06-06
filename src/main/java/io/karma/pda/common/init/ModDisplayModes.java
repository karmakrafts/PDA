/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.api.display.DefaultDisplayResolution;
import io.karma.pda.api.display.DefaultDisplayType;
import io.karma.pda.api.display.DisplayModeSpec;
import io.karma.pda.common.PDAMod;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 05/06/2024
 */
public final class ModDisplayModes {
    // @formatter:off
    private ModDisplayModes() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void register() {
        PDAMod.LOGGER.info("Registering display modes");
        for (final var type : DefaultDisplayType.values()) {
            for (final var resolution : DefaultDisplayResolution.values()) {
                final var modeSpec = new DisplayModeSpec(resolution, type);
                PDAMod.DISPLAY_MODES.register(modeSpec.name(), () -> modeSpec);
            }
        }
    }
}
