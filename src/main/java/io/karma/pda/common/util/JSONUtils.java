package io.karma.pda.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.dom.Node;
import io.karma.pda.common.dom.NodeType;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public final class JSONUtils {
    public static final ObjectMapper MAPPER;
    public static final ObjectReader READER;
    public static final ObjectWriter WRITER;
    private static final String KEY_NODE_TYPE = "type";
    // @formatter:off
    private static final SimpleModule MODULE;
    // @formatter:on

    static { // @formatter:off
        MODULE = new SimpleModule(PDAMod.MODID, new Version(1, 0, 0, "", null, null));
        MODULE.addSerializer(Node.class, new NodeSerializer());
        MODULE.addDeserializer(Node.class, new NodeDeserializer());
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(MODULE);
        READER = MAPPER.reader();
        WRITER = MAPPER.writer(new DefaultPrettyPrinter()
            .withSeparators(Separators.createDefaultInstance().withRootSeparator("\n")));
    } // @formatter:on

    // @formatter:off
    private JSONUtils() {}
    // @formatter:on

    private static final class NodeSerializer extends StdSerializer<Node> {
        public NodeSerializer() {
            super(Node.class);
        }

        @Override
        public void serialize(final Node node, final JsonGenerator generator, final SerializerProvider provider) {
            final var object = MAPPER.createObjectNode();
            object.put(KEY_NODE_TYPE, node.getType().toString());
            node.serialize(object);
        }
    }

    private static final class NodeDeserializer extends StdDeserializer<Node> {
        public NodeDeserializer() {
            super(Node.class);
        }

        @Override
        public Node deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
            final var object = (ObjectNode) parser.getCodec().readTree(parser);
            final var typeName = object.get(KEY_NODE_TYPE).asText();
            final var type = NodeType.byName(typeName).orElseThrow();
            final var node = type.create();
            node.deserialize(object);
            return node;
        }
    }
}