/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.component.DefaultContainer;
import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.api.common.app.view.DefaultContainerView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface AppContext {
    void addView(final String name, final AppView view);

    default void addView(final String name, final Consumer<DefaultContainer> callback) {
        addView(name, new DefaultContainerView(name, callback));
    }

    @Nullable
    AppView removeView(final String name);

    void setFullscreen(final boolean isFullscreen);

    void showControls();

    void close();

    void suspend();
}
