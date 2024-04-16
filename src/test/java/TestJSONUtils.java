/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.util.JSONUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TestJSONUtils {
    @Test
    void testCompressDecompress() {
        final var node = JSONUtils.MAPPER.createObjectNode();
        node.put("value", "HELLO WORLD!");

        final var data = JSONUtils.compress(node);
        Assertions.assertNotEquals(0, data.length);

        final var reconstructed = JSONUtils.decompress(data, ObjectNode.class);
        Assertions.assertNotNull(reconstructed);
        final var subNode = reconstructed.get("value");
        Assertions.assertTrue(subNode.isTextual());
        Assertions.assertEquals("HELLO WORLD!", subNode.asText());
    }
}
