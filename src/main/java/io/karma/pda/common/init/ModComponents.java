/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.component.DefaultContainer;
import io.karma.pda.api.common.app.component.Label;
import io.karma.pda.api.common.util.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class ModComponents {
    // @formatter:off
    private ModComponents() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void register(final DeferredRegister<ComponentType<?>> register) {
        register.register("container",
            () -> new ComponentType<>(new ResourceLocation(Constants.MODID, "container"), DefaultContainer::new));
        register.register("label",
            () -> new ComponentType<>(new ResourceLocation(Constants.MODID, "label"), Label::new));
    }
}
