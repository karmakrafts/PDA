/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.network.ComponentCodec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TestComponentCodec {
    @Test
    void testTranscodeComponent() {
        TestHarness.init();

        final var component = TestHarness.COMPONENT.create();

        final var node = JSONUtils.MAPPER.createObjectNode();
        ComponentCodec.encode(node, component);
        TestHarness.logger.info("Encoded component:\n{}\n", node.toPrettyString());

        final var reconstructed = ComponentCodec.decode(node);
        Assertions.assertNotNull(reconstructed);
        TestHarness.logger.info("Decoded component: {} ", reconstructed);

        Assertions.assertSame(TestHarness.COMPONENT, reconstructed.getType());
        Assertions.assertEquals(component.getId(), reconstructed.getId());

        TestHarness.dispose();
    }

    @Test
    void testTranscodeCompressedComponent() {
        TestHarness.init();

        final var component = TestHarness.COMPONENT.create();

        final var data = ComponentCodec.encode(component);
        Assertions.assertNotEquals(0, data.length);
        TestHarness.logger.info("Encoded and compressed component:\n{}\n", data);

        final var reconstructed = ComponentCodec.decode(data);
        Assertions.assertNotNull(reconstructed);
        TestHarness.logger.info("Decompressed and decoded component: {} ", reconstructed);

        Assertions.assertSame(TestHarness.COMPONENT, reconstructed.getType());
        Assertions.assertEquals(component.getId(), reconstructed.getId());

        TestHarness.dispose();
    }

    @Test
    void testTranscodeContainer() {
        TestHarness.init();

        final var container = TestHarness.CONTAINER.create();
        final var component = TestHarness.COMPONENT.create();
        container.addChild(component);

        final var node = JSONUtils.MAPPER.createObjectNode();
        ComponentCodec.encode(node, container);
        TestHarness.logger.info("Encoded container:\n{}\n", node.toPrettyString());

        final var reconstructed = ComponentCodec.decode(node);
        Assertions.assertNotNull(reconstructed);
        TestHarness.logger.info("Decoded component: {} ", reconstructed);

        Assertions.assertSame(container.getType(), reconstructed.getType());
        Assertions.assertEquals(container.getId(), reconstructed.getId());

        final var children = container.getChildren();
        Assertions.assertEquals(1, children.size());
        final var child = children.stream().findFirst().orElseThrow();
        Assertions.assertSame(component.getType(), child.getType());
        Assertions.assertEquals(component.getId(), child.getId());

        TestHarness.dispose();
    }

    @Test
    void testTranscodeCompressedContainer() {
        TestHarness.init();

        final var container = TestHarness.CONTAINER.create();
        final var component = TestHarness.COMPONENT.create();
        container.addChild(component);

        final var data = ComponentCodec.encode(container);
        TestHarness.logger.info("Encoded and compressed container:\n{}\n", data);

        final var reconstructed = ComponentCodec.decode(data);
        Assertions.assertNotNull(reconstructed);
        TestHarness.logger.info("Decompressed and decoded component: {} ", reconstructed);

        Assertions.assertSame(container.getType(), reconstructed.getType());
        Assertions.assertEquals(container.getId(), reconstructed.getId());

        final var children = container.getChildren();
        Assertions.assertEquals(1, children.size());
        final var child = children.stream().findFirst().orElseThrow();
        Assertions.assertSame(component.getType(), child.getType());
        Assertions.assertEquals(component.getId(), child.getId());

        TestHarness.dispose();
    }
}
