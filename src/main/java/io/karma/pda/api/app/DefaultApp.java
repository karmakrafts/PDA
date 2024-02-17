package io.karma.pda.api.app;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public abstract class DefaultApp implements App {
    protected final AppType<?> type;

    public DefaultApp(final AppType<?> type) {
        this.type = type;
    }

    @Override
    public AppType<?> getType() {
        return type;
    }
}
