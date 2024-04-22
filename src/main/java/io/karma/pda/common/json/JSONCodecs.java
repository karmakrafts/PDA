/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.api.common.flex.FlexBorder;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.api.common.flex.FlexValue;
import io.karma.pda.api.common.util.Constants;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class JSONCodecs {
    public static final SimpleModule MODULE = new SimpleModule(Constants.MODID);

    // @formatter:off
    private JSONCodecs() {}
    // @formatter:on

    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public static void setup() {
        MODULE.addSerializer(FlexValue.class, new FlexValueSerializer());
        MODULE.addSerializer(FlexBorder.class, new FlexBorderSerializer());
        MODULE.addSerializer(FlexNode.class, new FlexNodeSerializer());
        MODULE.addSerializer(Component.class, new ComponentSerializer());
        MODULE.addSerializer(AppView.class, new AppViewSerializer());

        MODULE.addDeserializer(FlexValue.class, new FlexValueDeserializer());
        MODULE.addDeserializer(FlexBorder.class, new FlexBorderDeserializer());
        MODULE.addDeserializer(FlexNode.class, new FlexNodeDeserializer());
        MODULE.addDeserializer(Component.class, new ComponentDeserializer<>(Component.class));
        MODULE.addDeserializer(Container.class, new ComponentDeserializer<>(Container.class));
        MODULE.addDeserializer(AppView.class, new AppViewDeserializer());

        final var components = API.getComponentTypes();
        for (final var component : components) {
            final var type = (Class<Component>) component.getType();
            MODULE.addSerializer(type, new ComponentSerializer());
            MODULE.addDeserializer(type, new ComponentDeserializer<>(type));
        }

        API.getObjectMapper().registerModule(MODULE);
    }
}
