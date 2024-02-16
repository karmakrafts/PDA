package io.karma.pda.api.app;

import io.karma.pda.api.app.component.ContainerComponent;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public class DefaultApp implements App {
    protected final AppType<?> type;

    public DefaultApp(final AppType<?> type) {
        this.type = type;
    }

    @Override
    public AppType<?> getType() {
        return type;
    }

    @Override
    public void populate(final ContainerComponent container) {
        
    }
}
