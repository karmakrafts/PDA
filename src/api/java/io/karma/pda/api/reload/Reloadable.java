/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.reload;

import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Comparator;

/**
 * @author Alexander Hinze
 * @since 21/08/2024
 */
@FunctionalInterface
public interface Reloadable {
    int DEFAULT_PRIORITY = 0;
    Comparator<Reloadable> PREP_COMPARATOR = Comparator.comparingInt(Reloadable::getPrepareReloadPriority);
    Comparator<Reloadable> COMPARATOR = (a, b) -> Integer.compare(b.getReloadPriority(), a.getReloadPriority());

    default int getReloadPriority() {
        final var type = getClass();
        if (!type.isAnnotationPresent(ReloadPriority.class)) {
            return DEFAULT_PRIORITY;
        }
        return type.getAnnotation(ReloadPriority.class).value();
    }

    default int getPrepareReloadPriority() {
        final var type = getClass();
        if (!type.isAnnotationPresent(PrepareReloadPriority.class)) {
            return getReloadPriority(); // If no annotation is present, prep priority is same as reload priority
        }
        return type.getAnnotation(PrepareReloadPriority.class).value();
    }

    default void prepareReload(final ResourceManager manager) {
    }

    void reload(final ResourceManager manager);
}
