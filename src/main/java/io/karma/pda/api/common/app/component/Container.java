/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.sliced.slice.Slice;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public interface Container extends Component {
    void addChild(final Component child);

    void removeChild(final Component child);

    @Nullable
    Component findChild(final UUID uuid);

    Slice<Component> getChildren();
}
