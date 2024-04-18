/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.api.common.app.component.*;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.common.PDAMod;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;
import java.util.function.BiFunction;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class ModComponents {
    // @formatter:off
    private ModComponents() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void register() {
        PDAMod.LOGGER.info("Registering components");
        register("container", DefaultContainer::new);
        register("label", Label::new);
        register("button", Button::new);
        register("separator", Separator::new);
        register("image", Image::new);
        register("item_image", ItemImage::new);
        register("block_image", BlockImage::new);
        register("entity_image", EntityImage::new);
        register("recipe_image", RecipeImage::new);
        register("player_image", PlayerImage::new);
    }

    private static <C extends Component> void register(final String name,
                                                       final BiFunction<ComponentType<C>, UUID, C> factory) {
        PDAMod.COMPONENTS.register(name,
            () -> new ComponentType<>(new ResourceLocation(Constants.MODID, name), factory));
    }
}
