/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.common.app.event.ClickEvent;
import io.karma.pda.api.common.app.event.MouseMoveEvent;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.api.common.util.Identifiable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Component extends Identifiable {
    default void dispose() {
    }

    ComponentType<?> getType();

    @Nullable
    Component getParent();

    void setParent(final @Nullable Component parent);

    FlexNode getFlexNode();

    void requestUpdate();

    boolean needsUpdate();

    void onClicked(final Consumer<ClickEvent> callback);

    void onMouseOver(final Consumer<MouseMoveEvent> callback);

    void onMouseEnter(final Consumer<MouseMoveEvent> callback);

    void onMouseExit(final Consumer<MouseMoveEvent> callback);

    /**
     * Provides a side-safe callback to access the synchronizer.
     * Since this usually only happens on the client, this method provides
     * the required isolation to make the code less unsafe.
     *
     * @param callback The side-safe callback to invoke when on the right effective side (Dist).
     */
    static void doWithSynchronizer(final Supplier<Consumer<Synchronizer>> callback) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> callback.get().accept(Objects.requireNonNull(ClientAPI.getSessionHandler().getSession()).getSynchronizer()));
    }
}
