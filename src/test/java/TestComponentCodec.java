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
public final class TestComponentCodec extends AbstractMockedTest {
    @Test
    void testTranscodeComponent() {
        final var component = COMPONENT.create();

        final var node = JSONUtils.MAPPER.createObjectNode();
        ComponentCodec.encode(node, component);
        LOGGER.info("Encoded component:\n{}\n", node.toPrettyString());

        final var reconstructed = ComponentCodec.decode(node);
        Assertions.assertNotNull(reconstructed);
        LOGGER.info("Decoded component: {} ", reconstructed);

        Assertions.assertSame(COMPONENT, reconstructed.getType());
        Assertions.assertEquals(component.getId(), reconstructed.getId());
    }

    @Test
    void testTranscodeContainer() {
        final var container = CONTAINER.create();
        final var component = COMPONENT.create();
        container.addChild(component);

        final var node = JSONUtils.MAPPER.createObjectNode();
        ComponentCodec.encode(node, container);
        LOGGER.info("Encoded container:\n{}\n", node.toPrettyString());

        final var reconstructed = ComponentCodec.decode(node);
        Assertions.assertNotNull(reconstructed);
        LOGGER.info("Decoded component: {} ", reconstructed);

        Assertions.assertSame(container.getType(), reconstructed.getType());
        Assertions.assertEquals(container.getId(), reconstructed.getId());

        final var children = container.getChildren();
        Assertions.assertEquals(1, children.size());
        final var child = children.stream().findFirst().orElseThrow();
        Assertions.assertSame(component.getType(), child.getType());
        Assertions.assertEquals(component.getId(), child.getId());
    }
}
