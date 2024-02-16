package io.karma.pda.api.app;

import io.karma.pda.api.app.component.ContainerComponent;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public interface App {
    AppType<?> getType();

    void populate(final ContainerComponent container);
}
