/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.hook;

import io.karma.pda.mod.client.util.RenderSystemUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ExtendedRenderSystem {
    static ExtendedRenderSystem getInstance() {
        return (ExtendedRenderSystem) RenderSystemUtils.getInstance();
    }

    ExtendedShader getExtendedShader();

    void setExtendedShader(final Supplier<ExtendedShader> shader);
}