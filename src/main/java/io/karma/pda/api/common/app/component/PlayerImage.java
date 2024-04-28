/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.color.Color;
import io.karma.pda.api.common.state.MutableState;
import io.karma.pda.api.common.state.Synchronize;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class PlayerImage extends AbstractComponent {
    @Synchronize
    public final MutableState<UUID> player = MutableState.of(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"));
    @Synchronize
    public final MutableState<RenderType> renderType = MutableState.of(RenderType.HEAD_2D);
    @Synchronize
    public final MutableState<Color> background = MutableState.of(Color.NONE);
    @Synchronize
    public final MutableState<Color> foreground = MutableState.of(Color.NONE);

    public PlayerImage(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }

    public enum RenderType {
        HEAD_2D, HEAD_3D, BODY_2D, BODY_3D
    }
}
