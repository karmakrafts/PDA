/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.api.common.flex.FlexBorder;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.api.common.flex.FlexValue;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.PDAMod;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class JSONCodecs {
    public static final SimpleModule MODULE = new SimpleModule(Constants.MODID);

    // @formatter:off
    private JSONCodecs() {}
    // @formatter:on

    public static void setup() {
        PDAMod.LOGGER.debug("Initializing JSON codecs");

        MODULE.addSerializer(FlexValue.class, new FlexValueSerializer());
        MODULE.addSerializer(FlexBorder.class, new FlexBorderSerializer());
        MODULE.addSerializer(FlexNode.class, new FlexNodeSerializer());
        MODULE.addSerializer(Component.class, new ComponentSerializer());
        MODULE.addSerializer(AppView.class, new AppViewSerializer());

        MODULE.addDeserializer(FlexValue.class, new FlexValueDeserializer());
        MODULE.addDeserializer(FlexBorder.class, new FlexBorderDeserializer());
        MODULE.addDeserializer(FlexNode.class, new FlexNodeDeserializer());
        MODULE.addDeserializer(Component.class, new ComponentDeserializer());
        MODULE.addDeserializer(AppView.class, new AppViewDeserializer());

        JSONUtils.MAPPER.registerModule(MODULE);
    }
}
