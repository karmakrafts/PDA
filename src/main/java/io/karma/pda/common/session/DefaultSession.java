/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.session;

import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultSession implements Session {
    private final UUID id;
    private final SessionContext context;

    public DefaultSession(final UUID id, final SessionContext context) {
        this.id = id;
        this.context = context;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public SessionContext getContext() {
        return context;
    }
}
