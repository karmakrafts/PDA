/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.display;

import net.minecraft.network.chat.Component;

/**
 * @author Alexander Hinze
 * @since 05/06/2024
 */
public interface DisplayType {
    int getIndex();

    String getName();

    String getTranslatedName();

    float getGlitchFactor();

    int getGlitchBlocks();

    float getGlitchRate();

    float getPixelationFactor();

    default Component getTranslatedNameComponent() {
        return Component.literal(getTranslatedName());
    }
}
