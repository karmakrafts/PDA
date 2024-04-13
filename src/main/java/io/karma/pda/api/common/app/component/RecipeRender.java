/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.sync.Synced;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class RecipeRender extends AbstractComponent {
    public final Synced<ResourceLocation> recipe = Synced.withInitial(new ResourceLocation("bucket"));

    public RecipeRender(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}
