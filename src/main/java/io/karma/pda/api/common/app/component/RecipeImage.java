/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.sync.Sync;
import io.karma.pda.api.common.sync.Synced;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class RecipeImage extends AbstractComponent {
    @Sync
    public final Synced<ResourceLocation> recipe = Synced.of(new ResourceLocation("bucket"));

    public RecipeImage(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}
