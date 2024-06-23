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
    String getName();

    String getTranslatedName();

    default Component getTranslatedNameComponent() {
        return Component.literal(getTranslatedName());
    }
}
