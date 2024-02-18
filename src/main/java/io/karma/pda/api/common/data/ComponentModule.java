/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.data;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.karma.pda.api.common.app.component.Component;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public final class ComponentModule extends SimpleModule {
    public static final ComponentModule INSTANCE = new ComponentModule();

    private ComponentModule() {
        super("pda.component", new Version(1, 0, 0, "stable", null, null));
        addSerializer(Component.class, new ComponentSerializer());
        addDeserializer(Component.class, new ComponentDeserializer());
    }
}
