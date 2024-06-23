/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 15/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface Type {
    Object getDefaultValue();

    String getName();

    int getComponentSize();

    int getComponentCount();

    default int getSize() {
        return getComponentSize() * getComponentCount();
    }
}
