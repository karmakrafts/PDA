/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.reload;

import io.karma.pda.api.API;
import io.karma.pda.api.reload.ReloadHandler;
import io.karma.pda.api.reload.Reloadable;
import io.karma.pda.mod.PDAMod;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * @author Alexander Hinze
 * @since 21/08/2024
 */
public final class DefaultReloadHandler implements ReloadHandler, PreparableReloadListener {
    public static final DefaultReloadHandler INSTANCE = new DefaultReloadHandler();
    private final ConcurrentLinkedQueue<Reloadable> objects = new ConcurrentLinkedQueue<>();

    // @formatter:off
    private DefaultReloadHandler() {}
    // @formatter:on

    @Internal
    public void setup() {
        PDAMod.LOGGER.debug("Initializing reload handler");
        ((ReloadableResourceManager) API.getResourceManager()).registerReloadListener(this);
    }

    @Override
    public void register(final Reloadable reloadable) {
        if (objects.contains(reloadable)) {
            return;
        }
        objects.add(reloadable);
    }

    @Override
    public void unregister(final Reloadable reloadable) {
        objects.remove(reloadable);
    }

    @Override
    public List<Reloadable> getObjects() {
        final var sorted = new ArrayList<>(objects);
        sorted.sort(Reloadable.COMPARATOR);
        return sorted;
    }

    private List<Reloadable> getPrepSortedObjects() {
        final var sorted = new ArrayList<>(objects);
        sorted.sort(Reloadable.PREP_COMPARATOR);
        return sorted;
    }

    @Internal
    @Override
    public @NotNull CompletableFuture<Void> reload(final @NotNull PreparationBarrier barrier,
                                                   final @NotNull ResourceManager manager,
                                                   final @NotNull ProfilerFiller prepProfiler,
                                                   final @NotNull ProfilerFiller reloadProfiler,
                                                   final @NotNull Executor backgroundExecutor,
                                                   final @NotNull Executor gameExecutor) {
        // @formatter:off
        return CompletableFuture.supplyAsync(() -> prepareAll(manager, getPrepSortedObjects()), gameExecutor)
            .thenCompose(barrier::wait)
            .thenAcceptAsync(results -> reloadAll(manager, getObjects()), gameExecutor);
        // @formatter:on
    }

    private @Nullable Void prepareAll(final ResourceManager manager, final List<Reloadable> objects) {
        PDAMod.LOGGER.debug("Preparing all resources");
        for (final var object : objects) {
            PDAMod.LOGGER.debug("Preparing resource {}", object);
            object.prepareReload(manager);
        }
        return null;
    }

    private void reloadAll(final ResourceManager manager, final List<Reloadable> objects) {
        PDAMod.LOGGER.debug("Reloading all resources");
        for (final var object : objects) {
            PDAMod.LOGGER.debug("Reloading resource {}", object);
            object.reload(manager);
        }
    }
}
