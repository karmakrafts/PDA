/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.karma.pda.api.common.sync.DefaultSyncCodec;
import io.karma.pda.api.common.util.JSONUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TestDefaultSyncCodec {
    public static final class TestClass {
        @JsonProperty
        public final String field1;
        @JsonProperty
        public final String field2;

        @JsonCreator
        public TestClass(@JsonProperty("field1") final String field1, @JsonProperty("field2") final String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof TestClass tc)) {
                return false;
            }
            return field1.equals(tc.field1) && field2.equals(tc.field2);
        }
    }

    @Test
    void testTranscode() {
        TestHarness.init();

        final var codec = new DefaultSyncCodec();

        final var object = new TestClass("foo", "bar");
        final var node = JSONUtils.MAPPER.createObjectNode();
        codec.encode("test", object, node);
        Assertions.assertFalse(node.isEmpty());
        TestHarness.logger.info("Encoded synchronized value:\n{}\n", node.toPrettyString());

        final var reconstructed = codec.decode("test", node);
        Assertions.assertEquals(object, reconstructed);

        TestHarness.dispose();
    }
}
