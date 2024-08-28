/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.file.Path;

/**
 * @author Alexander Hinze
 * @since 26/08/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ShaderCache {
    default void prepareProgram(final ShaderProgram program) {
    }

    /**
     * @param directory The cache directory to load from.
     * @param manager   The resource manager.
     * @param program   The shader program being loaded.
     * @return True if the program compilation may be skipped entirely.
     */
    default boolean loadProgram(final Path directory, final ResourceManager manager, final ShaderProgram program) {
        return false;
    }

    /**
     * @param directory The cache directory to save to.
     * @param manager   The resource manager.
     * @param program   The shader program being saved.
     */
    default void saveProgram(final Path directory, final ResourceManager manager, final ShaderProgram program) {
    }

    /**
     * Called when a shader object is being loaded/compiled.
     *
     * @param directory The cache directory to save to.
     * @param manager   The resource manager.
     * @param program   The shader program being prepared.
     * @param object    The shader object being prepared.
     */
    void save(final Path directory,
              final ResourceManager manager,
              final ShaderProgram program,
              final ShaderObject object);

    /**
     * Called before a shader object is being loaded/compiled.
     *
     * @param directory The cache directory to load from.
     * @param manager   The resource manager.
     * @param program   The shader program being prepared.
     * @param object    The shader object being prepared.
     * @return A new cancellation result.
     */
    CancellationResult load(final Path directory,
                            final ResourceManager manager,
                            final ShaderProgram program,
                            final ShaderObject object);

    record CancellationResult(boolean cancelCompile, boolean cancelLink) {
        public static final CancellationResult CANCEL_COMPILE = new CancellationResult(true, false);
        public static final CancellationResult CANCEL_LINK = new CancellationResult(false, true);
        public static final CancellationResult CANCEL_NONE = new CancellationResult(false, false);
    }
}
