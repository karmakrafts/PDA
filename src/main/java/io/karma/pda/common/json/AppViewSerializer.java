/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.karma.pda.api.common.app.view.AppView;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class AppViewSerializer extends StdSerializer<AppView> {
    public AppViewSerializer() {
        super(AppView.class);
    }

    @Override
    public void serialize(final AppView view, final JsonGenerator generator,
                          final SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("name", view.getName());
        generator.writeObjectField("container", view.getContainer());
        generator.writeEndObject();
    }
}
