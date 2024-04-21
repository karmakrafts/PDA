/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.karma.pda.api.common.flex.FlexBorder;
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
        MODULE.addDeserializer(FlexValue.class, new FlexValueDeserializer());
        MODULE.addDeserializer(FlexBorder.class, new FlexBorderDeserializer());
        JSONUtils.MAPPER.registerModule(MODULE);
    }
}
