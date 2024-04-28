/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.color.GradientFunction;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.common.PDAMod;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class JSONCodecs {
    // @formatter:off
    private JSONCodecs() {}
    // @formatter:on

    public static void register() {
        PDAMod.LOGGER.debug("Registering JSON codecs");
        final var module = new SimpleModule(Constants.MODID);
        module.addSerializer(GradientFunction.class, new GradientFunctionSerializer());
        module.addDeserializer(GradientFunction.class, new GradientFunctionDeserializer());
        API.getObjectMapper().registerModule(module);
    }
}
