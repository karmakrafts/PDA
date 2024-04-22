/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.api.common.app.view.DefaultView;
import io.karma.pda.api.common.util.JSONUtils;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class AppViewDeserializer extends StdDeserializer<AppView> {
    public AppViewDeserializer() {
        super(AppView.class);
    }

    @Override
    public AppView deserialize(final JsonParser parser,
                               final DeserializationContext deserializationContext) throws IOException {
        final var node = parser.getCodec().readTree(parser);
        final var name = ((TextNode) node.get("name")).asText();
        final var container = JSONUtils.MAPPER.treeToValue(node.get("container"), Container.class);
        return new DefaultView(name, container);
    }
}
