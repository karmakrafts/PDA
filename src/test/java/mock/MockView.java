/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package mock;

import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppContext;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.view.AppView;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
public final class MockView implements AppView {
    private final String name;
    private final Container container;

    public MockView(final String name, final Container container) {
        this.name = name;
        this.container = container;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void build(final App app, final AppContext context) {
    }

    @Override
    public Container getContainer() {
        return container;
    }
}
