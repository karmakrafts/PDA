/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.reload;

import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * @author Alexander Hinze
 * @since 21/08/2024
 */
@FunctionalInterface
public interface Reloadable<T> {
    Comparator<Reloadable<?>> COMPARATOR = (a, b) -> Integer.compare(b.getReloadPriority(), a.getReloadPriority());

    default int getReloadPriority() {
        final var type = getClass();
        if (!type.isAnnotationPresent(ReloadPriority.class)) {
            return 0;
        }
        return type.getAnnotation(ReloadPriority.class).value();
    }

    default @Nullable T prepareReload(final ResourceManager manager) {
        return null;
    }

    void reload(final @Nullable T value, final ResourceManager manager);
}
