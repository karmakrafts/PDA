package io.karma.pda.api.common.app;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public abstract class AbstractApp implements App {
    protected final AppType<?> type;

    public AbstractApp(final AppType<?> type) {
        this.type = type;
    }

    @Override
    public AppType<?> getType() {
        return type;
    }
}
