/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.network.AppViewCodec;
import mock.MockView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TestAppViewCodec {
    @Test
    void testTranscode() {
        TestHarness.init();

        final var container = TestHarness.CONTAINER.create();
        final var component = TestHarness.COMPONENT.create();
        container.addChild(component);

        final var view = new MockView("foo", container);
        final var node = JSONUtils.MAPPER.createObjectNode();
        AppViewCodec.encode(node, view);
        TestHarness.logger.info("Encoded view:\n{}\n", node.toPrettyString());

        final var reconstructed = AppViewCodec.decode(node);
        Assertions.assertNotNull(reconstructed);
        TestHarness.logger.info("Decoded view: {}", reconstructed);
        Assertions.assertEquals(view.getName(), reconstructed.getName());

        final var reconContainer = reconstructed.getContainer();
        Assertions.assertNotNull(reconContainer);
        Assertions.assertSame(container.getType(), reconContainer.getType());
        Assertions.assertEquals(container.getId(), reconContainer.getId());

        final var reconComponent = reconContainer.getChildren().stream().findFirst().orElseThrow();
        Assertions.assertSame(component.getType(), reconComponent.getType());
        Assertions.assertEquals(component.getId(), reconComponent.getId());

        TestHarness.dispose();
    }

    @Test
    void testTranscodeCompressed() {
        TestHarness.init();

        final var container = TestHarness.CONTAINER.create();
        final var component = TestHarness.COMPONENT.create();
        container.addChild(component);

        final var view = new MockView("foo", container);
        final var data = AppViewCodec.encode(view);
        Assertions.assertNotEquals(0, data.length);
        TestHarness.logger.info("Encoded and compressed view:\n{}\n", data);

        final var reconstructed = AppViewCodec.decode(data);
        Assertions.assertNotNull(reconstructed);
        TestHarness.logger.info("Decompressed and decoded view: {}", reconstructed);
        Assertions.assertEquals(view.getName(), reconstructed.getName());

        final var reconContainer = reconstructed.getContainer();
        Assertions.assertNotNull(reconContainer);
        Assertions.assertSame(container.getType(), reconContainer.getType());
        Assertions.assertEquals(container.getId(), reconContainer.getId());

        final var reconComponent = reconContainer.getChildren().stream().findFirst().orElseThrow();
        Assertions.assertSame(component.getType(), reconComponent.getType());
        Assertions.assertEquals(component.getId(), reconComponent.getId());

        TestHarness.dispose();
    }
}
